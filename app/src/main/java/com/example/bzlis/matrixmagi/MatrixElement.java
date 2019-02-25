package com.example.bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
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
/*
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (DataBag.getInstance().getCurrView().shouldUpdate) {
                    for (EditGridLayout layout : DataBag.getInstance().getData())
                        layout.switchBorderColor(-1);
                    DataBag.getInstance().getCurrView().shouldUpdate = false;
                    //invalidate();
                }
                DataBag.getInstance().getCurrView().hide();
                DataBag.getInstance().requestSelected((MatrixElement)v);
                DataBag.getInstance().showBoard((MatrixElement)v);
                if (((((MatrixElement)v).getText() == null) || ((MatrixElement)v).getText().toString().equals("")) && (((MatrixElement)v).getHint() != null)) {
                    try {
                        if (!((MatrixElement)v).trueValue.equals(ComplexForm.parse(((MatrixElement)v).trueValue.getPrettyString())))
                            Toast.makeText(((MatrixElement)v).context, trueValue.getFullString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e){}
                }
                return true;
            }
        });
*/
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
