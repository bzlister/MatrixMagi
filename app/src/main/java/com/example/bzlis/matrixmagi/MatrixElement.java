package com.example.bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class MatrixElement extends android.support.v7.widget.AppCompatEditText {


    private MatrixElement next;
    private Context context;
    private Double trueValue;
    private boolean complex = false;

    public MatrixElement(Context context){
        super(context);
        this.context = context;
       // this.setKeyListener(DigitsKeyListener.getInstance(".0123456789-"));
       this.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
       // this.setRawInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        this.setKeyListener(DigitsKeyListener.getInstance("0123456789.-"));
        this.setBackground(null);
        this.setTypeface(Typeface.SERIF, Typeface.ITALIC);
        this.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        /*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            this.setAutoSizeTextTypeWithDefaults(AUTO_SIZE_TEXT_TYPE_UNIFORM);
            */
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((((MatrixElement)v).getText() == null) || ((MatrixElement)v).getText().toString().equals("")) && (((MatrixElement)v).getHint() != null)) {
                    try {
                        if (!(((MatrixElement)v).getHint().toString().equals("") || ((MatrixElement)v).trueValue.doubleValue() == (DecimalFormat.getInstance(Locale.getDefault()).parse(((MatrixElement)v).getPrettyString()).doubleValue())))
                            Toast.makeText(((MatrixElement)v).context, String.format(Locale.getDefault(), "%g", trueValue), Toast.LENGTH_SHORT).show();
                    } catch (Exception e){}
                }
            }
        });
    }

    public void setNext(MatrixElement next){
        this.next = next;
    }

    public MatrixElement getNext(){
        return this.next;
    }

    public Double getTrueValue(){
        return this.trueValue;
    }

    public void setTrueValue(Double number){
        this.trueValue = number;
    }

    public String getPrettyString(){
        String s = "";
        if (trueValue == null)
            trueValue = 0.0;
        else if (trueValue == (long)(1.0*trueValue))
            s = String.format(Locale.getDefault(), "%d", (int)(1.0*trueValue));
        else {
            s = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(trueValue);
            /*
            if (s.replace("-","").length() == 1)
                s = s + "aaaaaaaa";
                */
        }
        if (s.length() > 5)
            s = new DecimalFormat("0.##E0", DecimalFormatSymbols.getInstance(Locale.getDefault())).format(trueValue);
        return s;
    }
}
