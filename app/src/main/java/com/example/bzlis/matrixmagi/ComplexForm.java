package com.example.bzlis.matrixmagi;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class ComplexForm {
    private Double real;
    private Double complex;

    public ComplexForm(Double real, Double complex){
        this.real = real;
        this.complex = complex;
    }

    public ComplexForm(Double real){
        this.real = real;
        this.complex = 0.0;
    }

    public static ComplexForm add(ComplexForm a, ComplexForm b){
        return new ComplexForm(a.real + b.real, a.complex + b.complex);
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
        return this.real.equals(that.real) && this.complex.equals(that.complex);
    }

    @Override
    public String toString(){
        String s = real.toString();
        if (complex != 0.0){
            if (complex > 0)
                s += "+";
            s += complex.toString()+"i";
        }
        return s;
    }

    public String getPrettyString(){
        String s = "";
        if (real == (long)(1.0*real))
            s = String.format(Locale.getDefault(), "%d", (int)(1.0*real));
        else {
            s = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(real);
            /*
            if (s.replace("-","").length() == 1)
                s = s + "aaaaaaaa";
                */
        }
        if (s.length() > 5)
            s = new DecimalFormat("0.##E0", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(real);
        if (complex != 0.0) {
            if (complex > 0)
                s+="+";
            else
                s+="-";
            if (complex == (long) (1.0 * complex))
                s += String.format(Locale.getDefault(), "%d", (int) (1.0 * Math.abs(complex)));
            else {
                s += new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(Math.abs(complex));
            /*
            if (s.replace("-","").length() == 1)
                s = s + "aaaaaaaa";
                */
            }
            if (s.length() > 5)
                s += new DecimalFormat("0.##E0", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(Math.abs(complex));
        }
        return s;
    }



}
