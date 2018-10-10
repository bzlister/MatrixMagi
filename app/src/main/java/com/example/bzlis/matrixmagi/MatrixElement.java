package com.example.bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Typeface;
import android.text.method.DigitsKeyListener;

public class MatrixElement extends android.support.v7.widget.AppCompatEditText {


    private MatrixElement next;

    public MatrixElement(Context context){
        super(context);
        this.setKeyListener(DigitsKeyListener.getInstance(".0123456789-"));
        this.setBackground(null);
        this.setTypeface(Typeface.SERIF, Typeface.ITALIC);
    }

    public void setNext(MatrixElement next){
        this.next = next;
    }

    public MatrixElement getNext(){
        return this.next;
    }
}
