package bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.bzlis.matrixmagi.R;

import java.util.ArrayList;
import java.util.Calendar;

public class EditGridLayout extends RelativeLayout {

    public int cellLength;
    private static int count = 0;
    private int secret;
    private int matRows;
    private int matCols;
    private Matrix matrix;
    private MatrixElement[][] edits;
    private GridLayout grid;
    protected final float thick = 0.3f;
    private int borderColor = Color.rgb(35, 188, 196);
    private ImageView border;
    private static boolean mutated;
    private float oldX;
    private float oldY;
    private int lastAction = MotionEvent.ACTION_DOWN;
    private float tranX;
    private float tranY;

    private OnTouchListener elementListener = new OnTouchListener() {
        private static final int MAX_CLICK_DURATION = 150;
        private long startClickTime;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            DataBag.getInstance().deletor.setVisibility(View.GONE);
            if (lastAction == MotionEvent.ACTION_DOWN || lastAction == MotionEvent.ACTION_UP) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    lastAction = MotionEvent.ACTION_DOWN;
                } else {
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if (clickDuration < MAX_CLICK_DURATION && motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (DataBag.getInstance().getCurrView().shouldUpdate) {
                            for (EditGridLayout layout : DataBag.getInstance().getData())
                                layout.switchBorderColor(-1);
                            DataBag.getInstance().getCurrView().shouldUpdate = false;
                        }
                        DataBag.getInstance().getCurrView().hide();
                        DataBag.getInstance().requestSelected((MatrixElement)view);
                        DataBag.getInstance().showBoard((MatrixElement)view);
                        if (((((MatrixElement)view).getText() == null) || ((MatrixElement)view).getText().toString().equals("")) && (((MatrixElement)view).getHint() != null)) {
                            try {
                                if (!((MatrixElement)view).trueValue.equals(ComplexForm.parse(((MatrixElement)view).trueValue.getPrettyString())))
                                    Toast.makeText(DataBag.getInstance().getCurrView().getContext(), ((MatrixElement)view).trueValue.getFullString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e){}
                        }
                        lastAction = MotionEvent.ACTION_UP;
                    } else if (clickDuration >= MAX_CLICK_DURATION && motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        lastAction = MotionEvent.ACTION_MOVE;
                        tranX = motionEvent.getRawX() - EditGridLayout.this.getX();
                        tranY = motionEvent.getRawY() - EditGridLayout.this.getY();
                        EditGridLayout.this.moveIt(motionEvent);
                    }
                }
            } else
                EditGridLayout.this.moveIt(motionEvent);
            return true;
        }
    };


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

        border = new ImageView(this.getContext());
        border.setLayoutParams(new LayoutParams(Math.round(cellLength*(matCols+thick)), Math.round(cellLength*(matRows+thick))));
        border.setTranslationX(thick*cellLength*0.5f);
        border.setTranslationY(thick*cellLength*0.5f);
        border.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)border.getBackground()).setColor(Color.TRANSPARENT);
        ((GradientDrawable)border.getBackground()).setStroke(Math.round(cellLength*thick*0.5f), borderColor);

        this.addView(border);
        this.addView(grid);
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

    protected boolean moveIt(MotionEvent me){
        DataBag.getInstance().deletor.setVisibility(View.GONE);
        if (me.getAction() == MotionEvent.ACTION_MOVE) {
            DataBag.getInstance().getCurrView().mag.setVisibility(VISIBLE);
            DataBag.getInstance().getCurrView().eigen.setVisibility(VISIBLE);
            DataBag.getInstance().getCurrView().det.setVisibility(VISIBLE);
            DataBag.getInstance().getCurrView().inv.setVisibility(VISIBLE);
            DataBag.getInstance().getCurrView().vvv.setVisibility(VISIBLE);
            DataBag.getInstance().getCurrView().ttt.setVisibility(VISIBLE);
            DataBag.getInstance().getCurrView().lews.setVisibility(VISIBLE);
            int len = getCellLength();
            int x0 = Math.round(me.getRawX()-tranX+len*getThickness());
            int y0 = Math.round(me.getRawY()-tranY+len*getThickness());
            int x1 = x0 + len * getNumCols();
            int y1 = y0 + len * getNumRows();
            if (DataBag.getInstance().isOccupied(x0, y0, x1, y1, getSecret(), false) < 0) {
                setX(me.getRawX()-tranX);
                setY(me.getRawY()-tranY);
            }
            if (me.getRawY() >= len * (DataBag.getInstance().getCurrView().numRows - 2))
                DataBag.getInstance().getCurrView().makeGlow(me);
            else
                DataBag.getInstance().getCurrView().makeDim();
        }
        if (mutated) {
            for (EditGridLayout layout : DataBag.getInstance().getData())
                layout.switchBorderColor(-1);
            invalidate();
        }
        DataBag.getInstance().getCurrView().hide();
        if (me.getAction() == MotionEvent.ACTION_UP) {
            DataBag.getInstance().getCurrView().makeDim();
            DataBag.getInstance().getCurrView().mag.setVisibility(INVISIBLE);
            DataBag.getInstance().getCurrView().eigen.setVisibility(INVISIBLE);
            DataBag.getInstance().getCurrView().det.setVisibility(INVISIBLE);
            DataBag.getInstance().getCurrView().inv.setVisibility(INVISIBLE);
            DataBag.getInstance().getCurrView().vvv.setVisibility(INVISIBLE);
            DataBag.getInstance().getCurrView().ttt.setVisibility(INVISIBLE);
            DataBag.getInstance().getCurrView().lews.setVisibility(INVISIBLE);
            int len = getCellLength();
            setX(len * (Math.round((getX() + len * getThickness()) / len) - getThickness()));
            setY(len * (Math.round((getY() + len * getThickness()) / len) - getThickness()));
            int x0 = Math.round(getX());
            int y0 = Math.round(getY());
            int x1 = Math.round(x0 + len * (getNumCols() + 2 * getThickness()));
            int y1 = Math.round(y0 + len * (getNumRows() + 2 * getThickness()));
            int secret;
            if ((secret = DataBag.getInstance().isOccupied(x0, y0, x1, y1, getSecret(), true)) >= 0) {
                hideKeyboard();
                int a, b;
                EditGridLayout other = DataBag.getInstance().getData(secret);
                if (getEncsMatrix() instanceof Scalar || other.getEncsMatrix() instanceof Scalar) {
                    if (!(getEncsMatrix() instanceof Scalar))
                        DataBag.getInstance().getCurrView().scalarButtons(getSecret(), other.getSecret());
                    else if (!(other.getEncsMatrix() instanceof Scalar))
                        DataBag.getInstance().getCurrView().scalarButtons(other.getSecret(), getSecret());
                } else {
                    if (x0 < other.getX() || (x0 == other.getX() && y0 < other.getY())) {
                        a = getSecret();
                        b = other.getSecret();
                    } else {
                        a = other.getSecret();
                        b = getSecret();
                    }
                    DataBag.getInstance().getCurrView().arithButtons(a, b);
                }
            } else if (me.getRawY() >= len * (DataBag.getInstance().getCurrView().numRows - 2)) {
                try {
                    int spawnX = Math.round(oldX + len * thick);
                    int spawnY = Math.round(oldY + len * thick);
                    if (!(getEncsMatrix() instanceof Scalar)) {
                        if (me.getRawX() < DataBag.getInstance().getCurrView().det.getX()) {
                            Scalar mag = new Scalar(new ComplexForm(getEncsMatrix().mag()));
                            DataBag.getInstance().getCurrView().makeEditGrid(mag, new Point(spawnX, spawnY));
                        } else if (me.getRawX() < DataBag.getInstance().getCurrView().inv.getX()) {
                            Scalar det = new Scalar(getEncsMatrix().det());
                            DataBag.getInstance().getCurrView().makeEditGrid(det, new Point(spawnX, spawnY));
                        } else if (me.getRawX() < DataBag.getInstance().getCurrView().ttt.getX()) {
                            Matrix inv = getEncsMatrix().inverse();
                            DataBag.getInstance().getCurrView().makeEditGrid(inv, new Point(spawnX, spawnY));


                        } else if (me.getRawX() < DataBag.getInstance().getCurrView().eigen.getX()) {
                            Matrix tpose = getEncsMatrix().transpose();
                            DataBag.getInstance().getCurrView().makeEditGrid(tpose, new Point(spawnX, spawnY));
                        } else if (me.getRawX() < DataBag.getInstance().getCurrView().vvv.getX()){
                            try {
                                ArrayList<Scalar> lambda = getEncsMatrix().eigenValue();
                                for (int i = 0; i < lambda.size(); i++)
                                    DataBag.getInstance().getCurrView().makeEditGrid(lambda.get(i), new Point(spawnX + len * i, spawnY + len * i));
                            } catch (ArithmeticException e) {
                                Toast.makeText(DataBag.getInstance().getCurrView().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (me.getRawX() < DataBag.getInstance().getCurrView().lews.getX()) {
                            try {
                                ArrayList<Matrix> evecs = getEncsMatrix().eigenVector();
                                for (int i = 0; i < evecs.size(); i++)
                                    DataBag.getInstance().getCurrView().makeEditGrid(evecs.get(i), new Point(spawnX + len * i, spawnY));
                            } catch (ArithmeticException e) {
                                Toast.makeText(DataBag.getInstance().getCurrView().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (me.getRawX() >= DataBag.getInstance().getCurrView().lews.getX()){
                        ((ViewGroup) DataBag.getInstance().getCurrView().getParent()).removeView(this);
                        DataBag.getInstance().removeData(this);
                        DataBag.getInstance().getCurrView().invalidate();
                        if (DataBag.getInstance().deltut && DataBag.getInstance().getData().size() > 2) {
                            Toast.makeText(DataBag.getInstance().getCurrView().getContext(), "Shake device to delete all!", Toast.LENGTH_SHORT).show();
                            DataBag.getInstance().deltut = false;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    Toast.makeText(DataBag.getInstance().getCurrView().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                oldX = getX();
                oldY = getY();
            }
            lastAction = MotionEvent.ACTION_UP;
        }
        return true;
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
                    input = new MatrixElement(DataBag.getInstance().getCurrView().getContext());
                    edits[i][j] = input;
                }
                if ((i == 0) && (j == 0))
                    input.requestFocus();
                else
                    edits[(j == 0) ? i - 1 : i][(j == 0) ? matCols-1 : j-1].setNext(input);
                input.setTrueValue(matrix.getElement(i,j));
                input.setHint((input.getTrueValue().getPrettyString().equals("") ? "0" : input.getTrueValue().getPrettyString()));
                input.setOnTouchListener(elementListener);
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
                if (value.equals(""))
                    value = edits[i][j].getHint().toString();
                //This is so, when typing in a cell A, cell B doesn't change 1.23456789 to it's displayed value of 1.23
                if (!value.equals(edits[i][j].getTrueValue().getPrettyString())) {
                    ComplexForm cf;
                    try {
                        cf = ComplexForm.parse(value);
                        matrix.setElement(cf, i, j);
                        edits[i][j].setTrueValue(cf);
                        edits[i][j].setExcepMessage("");
                    } catch (Exception e){
                        edits[i][j].setExcepMessage(e.getMessage());
                    }
                }
            }
        }
        DataBag.getInstance().getCurrView().hide();
    }

    protected void blare(){
        String s = "";
        for (int i = 0; i < matRows; i++){
            for (int j = 0; j < matCols; j++) {
                if (!edits[i][j].getExcepMessage().equals("")) {
                    s += edits[i][j].getExcepMessage() + "\n";
                    edits[i][j].setText("0");
                    edits[i][j].setExcepMessage("");
                }
            }
        }
        if (!s.trim().equals(""))
            Toast.makeText(DataBag.getInstance().getCurrView().getContext(), s.trim(), Toast.LENGTH_LONG).show();
    }

    protected static void hideKeyboard(){
        DataBag.getInstance().hideBoard();
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
