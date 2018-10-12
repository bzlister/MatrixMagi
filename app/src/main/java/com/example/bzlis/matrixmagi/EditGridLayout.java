package com.example.bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

public class EditGridLayout extends RelativeLayout {

    private int cellLength;
    private static int count = 0;
    private int secret;
    private int matRows;
    private int matCols;
    private Matrix matrix;
    private MatrixElement[][] edits;
    private WorkerFragment workerFragment;
    private GridLayout grid;
    private final float thick = 0.5f;
    public boolean removed = false;
    private OnTouchListener myOnTouchListener;
    private int borderColor = Color.rgb(35, 188, 196);
    private ImageView border;
    private PixelGridView dad;
    private static boolean mutated;
/*
    public EditGridLayout(Context context, Matrix m, int cellLength, WorkerFragment workerFragment, Point top, int borderColor, PixelGridView dad){
        this(context, m.getNumRows(), m.getNumCols(), cellLength, workerFragment, top, m, borderColor, dad);
    }
    */

    public EditGridLayout(Context context, final int cellLength, final WorkerFragment workerFragment, Point top, Matrix m, PixelGridView dad){
        super(context);
        this.cellLength = cellLength;
        this.matrix = m;
        if (m instanceof Scalar)
            borderColor = Color.GRAY;
        this.matRows = m.getNumRows();
        this.matCols = m.getNumCols();
        this.edits = new MatrixElement[matRows][matCols];
        this.workerFragment = workerFragment;
        this.setX(top.x-cellLength*thick);
        this.setY(top.y-cellLength*thick);
        this.grid = new GridLayout(this.getContext());
        this.secret = count;
        this.dad = dad;
        count++;
        init();
        grid.setTranslationX(cellLength*thick);
        grid.setTranslationY(cellLength*thick);
        this.setLayoutParams(new LayoutParams(Math.round(cellLength*(matCols+2*thick)), Math.round(cellLength*(matRows + 2*thick))));
        grid.setLayoutParams(new LayoutParams(cellLength*matCols, cellLength*matRows));
        myOnTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me){
                EditGridLayout edit = (EditGridLayout)v;
                edit.dad.hideButtons();
                if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                    edit.dad.lews.setVisibility(VISIBLE);
                    int len = edit.getCellLength();
                    int x0 = len*Math.round(me.getRawX()/len);
                    int y0 = len*Math.round(me.getRawY()/len);
                    int x1 = x0 + len*edit.getNumCols();
                    int y1 = y0 + len*edit.getNumRows();
                    if (edit.getWorkerFragment().isOccupied(x0, y0, x1, y1, edit.getSecret(), false) < 0) {
                        edit.setX(me.getRawX() - len*thick);
                        edit.setY(me.getRawY() - len*thick);
                    }
                }
                if (mutated){
                    for (EditGridLayout layout : ((EditGridLayout)v).workerFragment.getData())
                        layout.switchBorderColor(-1);
                    invalidate();
                }
                if (me.getAction() == MotionEvent.ACTION_UP){
                    edit.dad.lews.setVisibility(INVISIBLE);
                    int len = edit.getCellLength();
                    edit.setX(len*(Math.round((edit.getX()+len*edit.getThickness())/len)-edit.getThickness()));
                    edit.setY(len*(Math.round((edit.getY()+len*edit.getThickness())/len)-edit.getThickness()));
                    int x0 = Math.round(edit.getX());
                    int y0 = Math.round(edit.getY());
                    int x1 = Math.round(x0 + len*(edit.getNumCols() + 2*edit.getThickness()));
                    int y1 = Math.round(y0 + len*(edit.getNumRows() + 2*edit.getThickness()));
                    int secret = 0;
                    if ((secret = edit.getWorkerFragment().isOccupied(x0, y0, x1, y1, edit.getSecret(), true)) >= 0) {
                        int a, b;
                        EditGridLayout other = edit.workerFragment.getData(secret);
                        if (x0 < other.getX() || (x0 == other.getX() && y0 < other.getY())){
                            a = edit.getSecret();
                            b = other.getSecret();
                        }
                        else{
                            a = other.getSecret();
                            b = edit.getSecret();
                        }
                        edit.dad.arithButtons(a, b);
                    }
                    else if ((edit.getActualX() >= len*(edit.dad.numColumns-2)) && (edit.getActualY() >= len*(edit.dad.numRows-2))){
                        ((ViewGroup)edit.dad.getParent()).removeView(edit);
                        edit.workerFragment.removeData(edit);
                        edit.dad.invalidate();
                    }
                }
                return true;
            }
        };

        border = new ImageView(this.getContext());
        border.setLayoutParams(new LayoutParams(Math.round(cellLength*(matCols+thick)), Math.round(cellLength*(matRows+thick))));
        border.setTranslationX(thick*cellLength*0.5f);
        border.setTranslationY(thick*cellLength*0.5f);
        border.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)border.getBackground()).setColor(Color.TRANSPARENT);
        ((GradientDrawable)border.getBackground()).setStroke(Math.round(cellLength*thick*0.5f), borderColor);
        this.addView(border);
        this.setOnTouchListener(myOnTouchListener);
        this.addView(grid);

       // this.bringChildToFront(grid);
        workerFragment.addData(this);
    }

    @Override
    public int hashCode(){
        return getSecret();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EditGridLayout that = (EditGridLayout) o;
        return secret == that.secret;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init(){
        for (int i = 0; i < matRows; i++) {
            for (int j = 0; j < matCols; j++) {
                MatrixElement input = edits[i][j];
                if (input == null) {
                    input = new MatrixElement(this.getContext());
                    edits[i][j] = input;
                }
                if ((i == 0) && (j == 0))
                    input.requestFocus();
                else
                    edits[(j == 0) ? i - 1 : i][(j == 0) ? matCols-1 : j-1].setNext(input);
                input.setHint(formatS(i, j));
                if ((i != matRows-1) || (j != matCols-1)) {
                    input.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_FULLSCREEN);
                    input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT)
                                ((MatrixElement)v).getNext().requestFocus();
                            return true;
                        }
                    });
                }
                else
                    input.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        fill();
                    }
                });
                input.setOnTouchListener(myOnTouchListener);
                setPos(i, j);
                grid.addView(input);
            }
        }
    }

    private String formatS(int i, int j){
        String s = "";
        if (matrix.getElement(i, j) == (long)(1.0*matrix.getElement(i, j)))
            s = String.format(Locale.getDefault(), "%d", (int)(1.0*matrix.getElement(i, j)));
        else
            s = String.format(Locale.getDefault(), "%g", matrix.getElement(i, j));
        return s;
    }

    private void fill(){
        for (int i = 0; i < matRows; i++){
            for (int j = 0; j < matCols; j++){
                String value = edits[i][j].getText().toString();
                if (value == null)
                    value = "0";
                Double num;
                try{
                    num = Double.parseDouble(value);
                } catch (NumberFormatException n){
                    num = 0.0;
                }
                matrix.setElement(num, i, j);
            }
        }
        workerFragment.addData(this);
    }

    private void setPos(int row, int column) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.width = cellLength;
        param.height = cellLength;
        param.setGravity(Gravity.CENTER);
        param.rowSpec = GridLayout.spec(row);
        param.columnSpec = GridLayout.spec(column);
        edits[row][column].setLayoutParams(param);
    }

    public int getNumRows(){
        return matRows;
    }

    public int getNumCols(){
        return matCols;
    }

    public Matrix getEncsMatrix(){
        return this.matrix;
    }

    public int getSecret(){
        return this.secret;
    }

    public int getActualX(){
        return Math.round(this.getX()+cellLength*thick);
    }

    public int getActualY(){
        return Math.round(this.getY()+cellLength*thick);
    }

    public int getCellLength(){
        return this.cellLength;
    }

    public float getThickness(){
        return this.thick;
    }
    private WorkerFragment getWorkerFragment(){
        return this.workerFragment;
    }

    protected void switchBorderColor(int color){
        if (color == -1) {
            ((GradientDrawable) border.getBackground()).setStroke(Math.round(cellLength * thick * 0.5f), borderColor);
            mutated = false;
        }
        else {
            ((GradientDrawable) border.getBackground()).setStroke(Math.round(cellLength * thick * 0.65f), color);
            mutated = true;
        }
    }
}
