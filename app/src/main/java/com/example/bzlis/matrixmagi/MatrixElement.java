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
    private ComplexForm trueValue;
    private String excepMessage = "";

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
                        // || ((MatrixElement)v).trueValue.doubleValue() == (DecimalFormat.getInstance(Locale.getDefault()).parse(((MatrixElement)v).getPrettyString()).doubleValue()))
                        if (((MatrixElement)v).trueValue.getFullString().length() > 5)
                            Toast.makeText(((MatrixElement)v).context, trueValue.getFullString(), Toast.LENGTH_SHORT).show();
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

    public ComplexForm getTrueValue(){
        return this.trueValue;
    }

    public void setTrueValue(ComplexForm cf){
        this.trueValue = cf;
    }

    public void setExcepMessage(String s){
        this.excepMessage = s;
    }

    public String getExcepMessage(){
        return excepMessage;
    }
}
