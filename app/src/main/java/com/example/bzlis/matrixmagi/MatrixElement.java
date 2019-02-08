package com.example.bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MatrixElement extends android.support.v7.widget.AppCompatEditText {


    private MatrixElement next;
    private Context context;
    private ComplexForm trueValue;
    private String excepMessage = "";

    public MatrixElement(Context context){
        super(context);
        this.context = context;
        //this.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        //this.setKeyListener(DigitsKeyListener.getInstance("0123456789.-/"));
        this.setInputType(InputType.TYPE_NULL);
        this.setBackground(null);
        this.setTypeface(Typeface.SERIF, Typeface.ITALIC);
        this.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
            /*
                DataBag.getInstance().setAdVis(View.GONE);
                DataBag.getInstance().boardOut = true;
                return false;
                */
                DataBag.getInstance().showBoard((MatrixElement)v);
                if (((((MatrixElement)v).getText() == null) || ((MatrixElement)v).getText().toString().equals("")) && (((MatrixElement)v).getHint() != null)) {
                    try {
                        // || ((MatrixElement)v).trueValue.doubleValue() == (DecimalFormat.getInstance(Locale.getDefault()).parse(((MatrixElement)v).getPrettyString()).doubleValue()))
                        if (((MatrixElement)v).trueValue.getFullString().length() > 5)
                            Toast.makeText(((MatrixElement)v).context, trueValue.getFullString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e){}
                }
                return false;
            }
        });

/*
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // DataBag.getInstance().boardOut = true;
                DataBag.getInstance().showBoard();
                if (((((MatrixElement)v).getText() == null) || ((MatrixElement)v).getText().toString().equals("")) && (((MatrixElement)v).getHint() != null)) {
                    try {
                        // || ((MatrixElement)v).trueValue.doubleValue() == (DecimalFormat.getInstance(Locale.getDefault()).parse(((MatrixElement)v).getPrettyString()).doubleValue()))
                        if (((MatrixElement)v).trueValue.getFullString().length() > 5)
                            Toast.makeText(((MatrixElement)v).context, trueValue.getFullString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e){}
                }
            }
        });
        */
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
