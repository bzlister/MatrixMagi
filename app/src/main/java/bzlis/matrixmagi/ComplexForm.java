package bzlis.matrixmagi;

import com.example.bzlis.matrixmagi.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ComplexForm {
    private Double real;
    private Double complex;
    private final double EPSILON = 1e-10;


    public ComplexForm(Number real, Number complex){
        this.real = real.doubleValue();
        this.complex = complex.doubleValue();
    }

    public ComplexForm(Number real){
        this.real = real.doubleValue();
        this.complex = 0.0;
    }

    public static ComplexForm parse(String s) throws Exception{
        if (s == null || s.equals(""))
            return new ComplexForm(0);
        ComplexForm cf = new ComplexForm(0, 0);
        try {
            char sep = ((DecimalFormat) NumberFormat.getInstance(Locale.getDefault())).getDecimalFormatSymbols().getDecimalSeparator();
            if (s.indexOf(sep) != s.lastIndexOf(sep))
                throw new Exception();
            ArrayList<String> nums = new ArrayList<>();
            int start = 0;
            while (start < s.length()){
                int end = start+1;
                while (end < s.length() && (s.charAt(end) != '+' && s.charAt(end) != '-')) {
                    end++;
                }
                nums.add(s.substring(start, end));
                start = end;
            }
            for (String num : nums){
                if (!num.equals("")) {
                    num = num.replace("+", "");
                    if (num.contains("i")) {
                        boolean negative = false;
                        if (num.contains("-")) {
                            num = num.replace("-", "");
                            negative = true;
                        }
                        if (num.indexOf('i') != 0 && num.indexOf('i') != num.length() - 1)
                            throw new Exception();
                        num = num.replace("i", "");
                        if (num.equals(""))
                            num = "1";
                        if (negative)
                            num = "-" + num;
                        cf = ComplexForm.add(cf, new ComplexForm(0, NumberFormat.getInstance(Locale.getDefault()).parse(num)));
                    }
                    else
                        cf = ComplexForm.add(cf, new ComplexForm(NumberFormat.getInstance(Locale.getDefault()).parse(num)));
                }
            }
        } catch (Exception p){
            throw new Exception(s +" " +  DataBag.getInstance().getCurrView().getResources().getString(R.string.cfExcep));
        }
        return cf;
    }

    public Double getReal(){
        return real;
    }

    public boolean isComplex(){
        return Math.abs(complex) > EPSILON;
    }

    public ComplexForm duplicate(){
        return new ComplexForm(real.doubleValue(), complex.doubleValue());
    }

    public Double magnitude(){
        return Math.sqrt(Math.pow(real, 2) + Math.pow(complex, 2));
    }

    public static ComplexForm sqrt(ComplexForm a){
        Double A = Math.sqrt((a.real + Math.sqrt(Math.pow(a.real, 2) + Math.pow(a.complex, 2)))/2);
        Double B = Math.sqrt((Math.sqrt(Math.pow(a.real, 2) + Math.pow(a.complex, 2)) - a.real)/2);
        return new ComplexForm(A, B);
    }

    public static ComplexForm add(ComplexForm a, ComplexForm b){
        ComplexForm retVal = new ComplexForm(a.real + b.real, a.complex + b.complex);
        return retVal;
    }

    public static ComplexForm sub(ComplexForm a, ComplexForm b){
        return new ComplexForm(a.real - b.real, a.complex - b.complex);
    }

    public static ComplexForm mult(ComplexForm a, ComplexForm b){
        ComplexForm retVal =  new ComplexForm(a.real*b.real - a.complex*b.complex, a.real*b.complex + a.complex*b.real);
        return retVal;
    }

    public ComplexForm conjugate(){
        return new ComplexForm(real.doubleValue(), -complex.doubleValue());
    }

    public ComplexForm correct(){
        if (this.real == Double.POSITIVE_INFINITY || Double.isNaN(this.real))
            this.real = Double.MAX_VALUE;
        if (this.complex == Double.POSITIVE_INFINITY || Double.isNaN(this.complex))
            this.complex = Double.MAX_VALUE;
        return this;
    }

    public static ComplexForm div(ComplexForm a, ComplexForm b){
        ComplexForm numerator = mult(a, new ComplexForm(b.real, -b.complex));
        Double denominator = mult(b, new ComplexForm(b.real, -b.complex)).real;
        numerator.real/=denominator;
        numerator.complex/=denominator;
        return numerator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexForm that = (ComplexForm) o;
        return (Math.abs(this.real.doubleValue() - that.real.doubleValue()) < EPSILON)
                && (Math.abs(this.complex.doubleValue() - that.complex.doubleValue()) < EPSILON);
    }

    public String getPrettyString(){
        String s = "";
        if (real != 0.0) {
            if (real == (long) (1.0 * real))
                s = String.format(Locale.getDefault(), "%d", (int) (1.0 * real));
            else {
                String option1 = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(real);
                String option2 = new DecimalFormat("0.##E0", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(real);
                if (option1.replace("-", "").length() < option2.replace("-", "").length()-2 || option1.length() > 7)
                    s = option2;
                else
                    s = option1;
            }
        }
        String t = "";
        String u = "";
        if (complex != 0.0) {
            u += (complex > 0) ?  "+" : "-";
            u += "i";
            if (complex != 1.0 && complex != -1.0) {
                if (complex == (long) (1.0 * complex))
                    t = String.format(Locale.getDefault(), "%d", (int) (1.0 * Math.abs(complex)));
                else {
                    String option1 = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(Math.abs(complex));
                    String option2 = new DecimalFormat("0.##E0", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(Math.abs(complex));
                    if (option1.replace("-", "").length() < option2.replace("-", "").length() - 2 || option1.length() > 7)
                        t = option2;
                    else
                        t = option1;
                }
            }
        }
        String retVal = (s+u+t).trim();
        if (retVal.length() > 0 && retVal.charAt(0) == '+')
            retVal = retVal.substring(1, retVal.length());
        return retVal;
    }

    public String getFullString(){
        String sr = "";
        String sc = "";
        if (real != 0.0)
            sr = real.toString();
        if (complex != 0.0)
            sc += ((complex < 0) ? "-" : "+") + "i" + Double.valueOf(Math.abs(complex)).toString();
        String retVal = sr+sc;
        if (retVal.length() > 0 && retVal.charAt(0) == '+')
            retVal = retVal.substring(1, retVal.length());
        return retVal;
    }
}
