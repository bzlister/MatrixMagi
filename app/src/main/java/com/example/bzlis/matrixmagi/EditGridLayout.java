package com.example.bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class EditGridLayout extends RelativeLayout {

    public int cellLength;
    private static int count = 0;
    private int secret;
    private int matRows;
    private int matCols;
    private Matrix matrix;
    private MatrixElement[][] edits;
    private GridLayout grid;
    protected final float thick = 0.5f;
    private OnTouchListener myOnTouchListener;
    private int borderColor = Color.rgb(35, 188, 196);
    private ImageView border;
    private static boolean mutated;
    private float oldX;
    private float oldY;


    public EditGridLayout(Context context, final int cellLength, Point top, Matrix m){
        super(context);
        this.cellLength = cellLength;
        this.matrix = m;
        if (m instanceof Scalar)
            borderColor = Color.GRAY;
        this.matRows = m.getNumRows();
        this.matCols = m.getNumCols();
        this.edits = new MatrixElement[matRows][matCols];
        this.setX(top.x-cellLength*thick);
        this.setY(top.y-cellLength*thick);
        this.oldX = this.getX();
        this.oldY = this.getY();
        this.grid = new GridLayout(this.getContext());
        this.secret = count;
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
                if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                    DataBag.getInstance().getCurrView().lews.setVisibility(VISIBLE);
                    DataBag.getInstance().getCurrView().eigen.setVisibility(VISIBLE);
                    DataBag.getInstance().getCurrView().det.setVisibility(VISIBLE);
                    DataBag.getInstance().getCurrView().inv.setVisibility(VISIBLE);
                    int len = edit.getCellLength();
                    int x0 = len*Math.round((me.getRawX()+len*thick)/len);
                    int y0 = len*Math.round((me.getRawY()-len*thick)/len);
                    int x1 = x0 + len*edit.getNumCols();
                    int y1 = y0 + len*edit.getNumRows();
                    if (DataBag.getInstance().isOccupied(x0, y0, x1, y1, edit.getSecret(), false) < 0) {
                        edit.setX(me.getRawX());
                        edit.setY(me.getRawY() - 2*len*thick);
                    }
                }
                if (mutated){
                    for (EditGridLayout layout : DataBag.getInstance().getData())
                        layout.switchBorderColor(-1);
                    invalidate();
                }
                DataBag.getInstance().getCurrView().hide();
                hideKeyboard(v);
                if (me.getAction() == MotionEvent.ACTION_UP){
                    DataBag.getInstance().getCurrView().lews.setVisibility(INVISIBLE);
                    DataBag.getInstance().getCurrView().eigen.setVisibility(INVISIBLE);
                    DataBag.getInstance().getCurrView().det.setVisibility(INVISIBLE);
                    DataBag.getInstance().getCurrView().inv.setVisibility(INVISIBLE);
                    int len = edit.getCellLength();
                    edit.setX(len*(Math.round((edit.getX()+len*edit.getThickness())/len)-edit.getThickness()));
                    edit.setY(len*(Math.round((edit.getY()+len*edit.getThickness())/len)-edit.getThickness()));
                    int x0 = Math.round(edit.getX());
                    int y0 = Math.round(edit.getY());
                    int x1 = Math.round(x0 + len*(edit.getNumCols() + 2*edit.getThickness()));
                    int y1 = Math.round(y0 + len*(edit.getNumRows() + 2*edit.getThickness()));
                    int secret;
                    if ((secret = DataBag.getInstance().isOccupied(x0, y0, x1, y1, edit.getSecret(), true)) >= 0) {
                        int a, b;
                        EditGridLayout other = DataBag.getInstance().getData(secret);

                        if (edit.getEncsMatrix() instanceof Scalar || other.getEncsMatrix() instanceof Scalar){
                            if (!(edit.getEncsMatrix() instanceof  Scalar))
                                DataBag.getInstance().getCurrView().scalarButtons(edit.getSecret(), other.getSecret());
                            else if (!(other.getEncsMatrix() instanceof Scalar))
                                DataBag.getInstance().getCurrView().scalarButtons(other.getSecret(), edit.getSecret());
                        }
                        else {
                            if (x0 < other.getX() || (x0 == other.getX() && y0 < other.getY())){
                                a = edit.getSecret();
                                b = other.getSecret();
                            }
                            else{
                                a = other.getSecret();
                                b = edit.getSecret();
                            }
                            DataBag.getInstance().getCurrView().arithButtons(a, b);
                        }
                    }
                    else if (edit.getActualY() >= len*(DataBag.getInstance().getCurrView().numRows-2)){
                        try {
                            if (edit.getActualX() < DataBag.getInstance().getCurrView().det.getX()) {
                                Matrix inv = edit.getEncsMatrix().inverse();
                                edit.setX(edit.oldX);
                                edit.setY(edit.oldY);
                                DataBag.getInstance().getCurrView().makeEditGrid(inv, new Point(Math.round(edit.oldX + len * thick), Math.round(edit.oldY + len * thick)));
                            } else if (edit.getActualX() < DataBag.getInstance().getCurrView().eigen.getX()) {
                                Scalar det = new Scalar(edit.getEncsMatrix().det());
                                edit.setX(edit.oldX);
                                edit.setY(edit.oldY);
                                DataBag.getInstance().getCurrView().makeEditGrid(det, new Point(Math.round(edit.oldX + len * thick), Math.round(edit.oldY + len * thick)));
                            } else if (edit.getActualX() < DataBag.getInstance().getCurrView().eigen.getX() + 2 * len) {
                                Matrix eigen = edit.getEncsMatrix().eigen();
                                edit.setX(edit.oldX);
                                edit.setY(edit.oldY);
                                DataBag.getInstance().getCurrView().makeEditGrid(eigen, new Point(Math.round(edit.oldX + len * thick), Math.round(edit.oldY + len * thick)));
                            } else {
                                ((ViewGroup) DataBag.getInstance().getCurrView().getParent()).removeView(edit);
                                DataBag.getInstance().removeData(edit);
                                DataBag.getInstance().getCurrView().invalidate();
                            }
                        } catch (IllegalArgumentException e){
                            Toast.makeText(DataBag.getInstance().getCurrView().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        edit.oldX = edit.getX();
                        edit.oldY = edit.getY();
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
        DataBag.getInstance().addData(this);
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
                input.setTrueValue(matrix.getElement(i,j));
                input.setHint(input.getPrettyString());
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
                setPos(i, j);
                grid.addView(input);
            }
        }
    }

    private void fill(){
        for (int i = 0; i < matRows; i++){
            for (int j = 0; j < matCols; j++){
                String value = edits[i][j].getText().toString();
                if (value.equals("")) {
                    value = edits[i][j].getHint().toString();
                    if (value.equals(""))
                        value = "0";
                }
                Number num;
                try{
                    num = NumberFormat.getInstance(Locale.getDefault()).parse(value);
                } catch (Exception n){
                    num = 0.0;
                }
                if (!value.equals(edits[i][j].getPrettyString()))
                    matrix.setElement(num.doubleValue(), i, j);
            }
        }
        DataBag.getInstance().getCurrView().hide();
       // DataBag.getInstance().addData(this); what does this do
    }

    protected static void hideKeyboard(View v){
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

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
        return DataBag.getInstance().getCurrView().cellLength;
    }

    public float getThickness(){
        return this.thick;
    }

    protected void switchBorderColor(int color){
        if (color == -1) {
            ((GradientDrawable) border.getBackground()).setStroke(Math.round(cellLength * thick * 0.5f), borderColor);
            mutated = false;;
        }
        else {
            ((GradientDrawable) border.getBackground()).setStroke(Math.round(cellLength * thick * 0.65f), color);
            mutated = true;
        }
    }


}
