package com.example.bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.view.View;

public class MatrixElement extends android.support.v7.widget.AppCompatEditText {


    private MatrixElement next;

    public MatrixElement(Context context){
        super(context);
       // this.setKeyListener(DigitsKeyListener.getInstance(".0123456789-"));
       this.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
       // this.setRawInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        this.setKeyListener(DigitsKeyListener.getInstance("0123456789.-"));
        this.setBackground(null);
        this.setTypeface(Typeface.SERIF, Typeface.ITALIC);
        this.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            this.setAutoSizeTextTypeWithDefaults(AUTO_SIZE_TEXT_TYPE_UNIFORM);
    }

    public void setNext(MatrixElement next){
        this.next = next;
    }

    public MatrixElement getNext(){
        return this.next;
    }
}
