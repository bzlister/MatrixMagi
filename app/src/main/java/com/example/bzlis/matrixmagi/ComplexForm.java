package com.example.bzlis.matrixmagi;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

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
        try {
            Number r = 0;
            Number c = 0;
            if (s.contains("i")) {
                if ((s.indexOf("i") != s.lastIndexOf("i")) || (((s.charAt(s.indexOf("i")-1) != '+') && (s.charAt(s.indexOf("i")-1) != '-')) && (s.indexOf("i") != s.length()-1)))
                    throw new ParseException("bla", 0);
                String[] pedi = new String[2];
                if (s.contains("+"))//++, -+
                    pedi = s.split("\\+");
                else if (s.indexOf('-') != s.lastIndexOf('-')) {//--
                    s = s.replaceFirst("-", "");
                    pedi = s.split("-");
                    pedi[0] = "-" + pedi[0];
                    pedi[1] = "-" + pedi[1];
                }
                else if (s.contains("-")){//+-
                    pedi = s.split("-");
                    pedi[1] = "-" + pedi[1];
                }
                pedi[1] = pedi[1].replace("i", "");
                if (pedi[0].length() == 0)
                    pedi[0]+="0";
                if (pedi[1].replace("-", "").length() == 0)
                    pedi[1]+="1";
                r = NumberFormat.getInstance(Locale.getDefault()).parse(pedi[0]);
                c = NumberFormat.getInstance(Locale.getDefault()).parse(pedi[1]);
            }
            else
                r = NumberFormat.getInstance(Locale.getDefault()).parse(s);
            return new ComplexForm(r, c);
        } catch (ParseException p){
            throw new Exception(s + " not a properly formatted number!");
        }
    }

    public Double getReal(){
        return real;
    }

    public boolean isComplex(){
        return complex != 0.0;
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
        return new ComplexForm(a.real + b.real, a.complex + b.complex);
    }

    public static ComplexForm sub(ComplexForm a, ComplexForm b){
        return new ComplexForm(a.real - b.real, a.complex - b.complex);
    }

    public static ComplexForm mult(ComplexForm a, ComplexForm b){
        return new ComplexForm(a.real*b.real - a.complex*b.complex, a.real*b.complex + a.complex*b.real);
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
        return (s+u+t).trim();
    }

    public String getFullString(){
        String sr = "";
        String sc = "";
        if (real != 0.0)
            sr = real.toString();
        if (complex != 0.0)
            sc += ((complex < 0) ? "-" : "+") + "i" + Double.valueOf(Math.abs(complex)).toString();
        return sr+sc;
    }
}
