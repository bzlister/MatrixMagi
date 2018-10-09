package com.example.bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import java.util.Locale;
import java.util.Random;

public class EditGridLayout extends RelativeLayout {

    private int cellLength;
    private static int count = 0;
    private int secret;
    private int matRows;
    private int matCols;
    private Matrix matrix;
    private EditText[][] edits;
    private WorkerFragment workerFragment;
    private GridLayout grid;
    private final float thick = 0.3f;
    private final int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.LTGRAY};
    public boolean removed = false;


    public EditGridLayout(Context context, Matrix m, int cellLength, WorkerFragment workerFragment, Point top){
        this(context, m.getNumRows(), m.getNumCols(), cellLength, workerFragment, top, m);
    }

    public EditGridLayout(Context context, int matRows, int matCols, final int cellLength, WorkerFragment workerFragment, Point top, Matrix m){
        super(context);
        this.cellLength = cellLength;
        this.matRows = matRows;
        this.matCols = matCols;
        if (m == null)
            this.matrix = new Matrix(matRows, matCols);
        else
            this.matrix = m;
        this.edits = new EditText[matRows][matCols];
        this.workerFragment = workerFragment;
        this.setX(top.x-cellLength*thick);
        this.setY(top.y-cellLength*thick);
        this.grid = new GridLayout(this.getContext());
        this.secret = count;
        count++;
        init();
        grid.setTranslationX(cellLength*thick);
        grid.setTranslationY(cellLength*thick);
        grid.setLayoutParams(new LayoutParams(cellLength*matCols, cellLength*matRows));
        //this.setLayoutParams(new RelativeLayout.LayoutParams((int)Math.round(cellLength*(0.5+matCols)), (int)Math.round(cellLength*(0.5+matRows))));
        OnTouchListener myOnTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me){
                if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                    EditGridLayout edit = (EditGridLayout)v.getParent();
                    int len = edit.getCellLength();


                    int x0 = len*Math.round(me.getRawX()/len);
                    int y0 = len*Math.round(me.getRawY()/len);
                    int x1 = x0 + len*edit.getNumCols();
                    int y1 = y0 + len*edit.getNumRows();
                    Log.i("pos", x0 + ", " + y0 + ", " + x1 + ", " + y1);
                    if (!edit.getWorkerFragment().isOccupied(x0, y0, x1, y1, edit.getSecret())) {
                        edit.setX(x0 - len*thick);
                        edit.setY(y0 - len*thick);
                    }
                }
                return true;
            }
        };

        int color = colors[new Random().nextInt(colors.length)];

        Button north = new Button(this.getContext());
        north.setLayoutParams(new LayoutParams((int)Math.round(cellLength*(matCols + 2*thick)), (int)Math.round(cellLength*thick)));
        north.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)north.getBackground()).setColor(color);
        north.setOnTouchListener(myOnTouchListener);
        this.addView(north);

        Button west = new Button(this.getContext());
        west.setLayoutParams(new LayoutParams((int)Math.round(cellLength*thick), (int)Math.round(cellLength*(matRows + 2*thick))));
        west.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)west.getBackground()).setColor(color);
        west.setOnTouchListener(myOnTouchListener);
        this.addView(west);

        Button east = new Button(this.getContext());
        east.setLayoutParams(new LayoutParams((int)Math.round(cellLength*thick), (int)Math.round(cellLength*(matRows + 2*thick))));
        east.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)east.getBackground()).setColor(color);
        east.setTranslationX(cellLength*(matCols+thick));
        east.setOnTouchListener(myOnTouchListener);
        this.addView(east);

        Button south = new Button(this.getContext());
        south.setLayoutParams(new LayoutParams((int)Math.round(cellLength*(matCols + 2*thick)), (int)Math.round(cellLength*thick)));
        south.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)south.getBackground()).setColor(color);
        south.setTranslationY(cellLength*(matRows+thick));
        south.setOnTouchListener(myOnTouchListener);
        this.addView(south);

        this.addView(grid);
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
                EditText input = new EditText(this.getContext());
                if ((i == 0) && (j == 0))
                    input.requestFocus();
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                input.setBackground(null);
                input.setHint(formatS(i, j));
                input.setTypeface(Typeface.SERIF, Typeface.ITALIC);
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
                edits[i][j] = input;
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
                if (value.equals(""))
                    value = "0";
                Double num = Double.parseDouble(value);
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

    private WorkerFragment getWorkerFragment(){
        return this.workerFragment;
    }
}
