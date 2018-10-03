package com.example.bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class MatrixElement extends android.support.v7.widget.AppCompatEditText{

    private int i;
    private int j;

    public MatrixElement(Context context, int i, int j){
        super(context);
        this.i = i;
        this.j = j;
        if ((i == 0) && (j == 0))
            this.requestFocus();
        this.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        this.setBackground(null);
        this.setHint("0");
        this.setTypeface(Typeface.SERIF, Typeface.ITALIC);
        this.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        //input.setOnTouchListener(touchListener);
    }

    public int getI(){
        return this.i;
    }

    public int getJ(){
        return this.j;
    }
}
