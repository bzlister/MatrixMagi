package com.example.bzlis.matrixmagi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class PixelGridView extends View {
    Point[] corners = new Point[2];
    protected int numColumns, numRows, numCells, cellLength;
    private Paint blackPaint = new Paint();
    private Paint redPaint = new Paint();
    private int buttonColor;
    private int buttonWidth;
    private int spacing;
    private boolean shouldUpdate = false;
    private Button[] myButs = new Button[12];
    private int scalarVis = View.GONE;
    private int arithVis = View.GONE;
    private int specialVis = View.GONE;
    protected TextView lews;
    protected TextView eigen;
    protected TextView inv;
    protected TextView det;
    public int id;
    public static int count;


    public PixelGridView(Context context){
        super(context, null);
        id = count;
        count++;
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(5);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        buttonColor = Color.rgb(255, 193, 102);
    }

    public void setNumCells(int numCells){
        this.numCells = numCells;
        calculateDimensions();
    }

    protected void makeTrashCan(){
        lews = new TextView(this.getContext());
        lews.setText("X");
        lews.setTextColor(Color.RED);
        lews.setTextSize(50);
        lews.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        lews.setTranslationX(cellLength*(numColumns-2));
        lews.setTranslationY(Math.round(cellLength*(numRows-2)));
        lews.setBackground(null);
        lews.setTextIsSelectable(false);
        lews.setVisibility(INVISIBLE);
        ((ViewGroup)this.getParent()).addView(lews, cellLength*2, cellLength*2);

        eigen = new TextView(this.getContext());
        eigen.setText("\u03bb");
        eigen.setBackground(null);
        eigen.setTextSize(40);
        eigen.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        eigen.setTranslationX(cellLength*4);
        eigen.setTranslationY(Math.round(cellLength*(numRows-2)));
        eigen.setTextIsSelectable(false);
        eigen.setVisibility(INVISIBLE);
        ((ViewGroup)this.getParent()).addView(eigen, cellLength*2, cellLength*2);

        det = new TextView(this.getContext());
        SpannableString ss = new SpannableString("|A|");
        ss.setSpan(new ForegroundColorSpan(Color.rgb(35, 188, 196)), 1, 2, 0);
        det.setText(ss);
        det.setBackground(null);
        det.setTextSize(40);
        det.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        det.setTranslationX(cellLength*2);
        det.setTranslationY(Math.round(cellLength*(numRows-2)));
        det.setTextIsSelectable(false);
        det.setVisibility(INVISIBLE);
        ((ViewGroup)this.getParent()).addView(det, cellLength*2, cellLength*2);

        inv = new TextView(this.getContext());
        Spanned expText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            expText = Html.fromHtml("A<sup>-1</sup>", Html.FROM_HTML_MODE_LEGACY);
        else
            expText = Html.fromHtml("A<sup>-1</sup>");
        SpannableString sr = new SpannableString(expText);
        sr.setSpan(new ForegroundColorSpan(Color.rgb(35, 188, 196)), 0, 1, 0);
        sr.setSpan(new RelativeSizeSpan(0.6f), 1, 3, 0);
        inv.setText(sr);
        inv.setBackground(null);
        inv.setTextSize(40);
        inv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        inv.setTranslationY(Math.round(cellLength*(numRows-2)));
        inv.setTextIsSelectable(false);
        inv.setVisibility(INVISIBLE);
        ((ViewGroup)this.getParent()).addView(inv, cellLength*2, cellLength*2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        if ((numCells < 1) || (getWidth() == 0) || (getHeight() == 0)) {
            return;
        }
        cellLength = (int)Math.round(Math.sqrt((getWidth()*getHeight()*1.0)/numCells));
        numColumns = (int)Math.round(getWidth()/(0.0 + cellLength));
        numRows = (int)Math.round(getHeight()/(0.0 + cellLength));;
        buttonWidth = Math.round(2*getWidth()/13f);
        spacing = Math.round(getWidth()/13f);
        String[] text = new String[]{"A + B", "A - B", "AB", "AX = B", "", "scalar","1x1 matrix", "", "", "cA","",""};

        for (int i = 0; i < myButs.length; i++){
            myButs[i] = new Button(DataBag.getInstance().getCurrView().getContext());
            if (i < 4)
                myButs[i].setVisibility(arithVis);
            else if (i == 5 || i == 6)
                myButs[i].setVisibility(scalarVis);
            else if (i == 9 || i == 10)
                myButs[i].setVisibility(specialVis);
            else
                myButs[i].setVisibility(GONE);
            myButs[i].setBackgroundResource(R.drawable.tags_rounded_corners);
            ((GradientDrawable)myButs[i].getBackground()).setColor(buttonColor);
            ((GradientDrawable)myButs[i].getBackground()).setStroke(0, buttonColor);
            myButs[i].setAllCaps(false);
            myButs[i].setTextColor(Color.GRAY);
            myButs[i].setText(colorize(text[i]));
            myButs[i].setTextSize(18);
            myButs[i].setTranslationX((i%4)*buttonWidth+((i%4)+1)*spacing);
            myButs[i].setTranslationY(cellLength*(numRows-2));
            ((ViewGroup)this.getParent()).addView(myButs[i]);
            myButs[i].setLayoutParams(new RelativeLayout.LayoutParams(buttonWidth, cellLength));
        }
        makeTrashCan();
       // if (DataBag.getInstance().getA() != DataBag.getInstance().getB())
        if (DataBag.getInstance().getArithOp())
            arithButtons(DataBag.getInstance().getA(), DataBag.getInstance().getB());
        DataBag.getInstance().getCurrView().invalidate();
        DataBag.getInstance().snapToGrid();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xE4E8EB);
        if (numColumns == 0 || numRows == 0)
            return;
        int width = getWidth();
        int height = getHeight();

        if (corners[0] != null)
            canvas.drawRect(corners[0].x, corners[0].y, corners[1].x, corners[1].y, redPaint);

        for (int i = 1; i < numColumns; i++)
            canvas.drawLine(i * cellLength, 0, i * cellLength, height, blackPaint);

        for (int i = 1; i < numRows; i++)
            canvas.drawLine(0, i * cellLength, width, i * cellLength, blackPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (shouldUpdate) {
            for (EditGridLayout layout : DataBag.getInstance().getData())
                layout.switchBorderColor(-1);
            shouldUpdate = false;
            //invalidate();
        }
        hide();
        EditGridLayout.hideKeyboard(this);
        int x = cellLength * Math.round(event.getX() / cellLength);
        int y = cellLength * Math.round(event.getY() / cellLength);
        if (corners[0] == null) {
            corners[0] = new Point(x, y);
            corners[1] = corners[0];
        } else
            corners[1] = new Point(x, y);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if ((corners[0].y - corners[1].y != 0) && (corners[0].x - corners[1].x != 0)) {
                if (DataBag.getInstance().isOccupied(Math.min(corners[0].x, corners[1].x), Math.min(corners[0].y, corners[1].y), Math.max(corners[0].x, corners[1].x), Math.max(corners[0].y, corners[1].y), -1, false) < 0) {
                    int matCols = Math.round(Math.abs(corners[0].x - corners[1].x) / cellLength);
                    int matRows = Math.round(Math.abs(corners[0].y - corners[1].y) / cellLength);
                    Point top = new Point(Math.min(corners[0].x, corners[1].x), Math.min(corners[0].y, corners[1].y));
                    if (matCols == 1 && matRows == 1)
                        scalarQuestionaire(top);
                    else
                        makeEditGrid(new Matrix(matRows, matCols), top);
                }
                else{
                    //delete menu?
                }
            }
            corners[0] = null;
            corners[1] = null;
        }
        invalidate();
        return true;
    }




    protected boolean arithmetic(int op, int a, int b){
        ViewGroup vg = (ViewGroup) this.getParent();
        EditGridLayout layoutB = DataBag.getInstance().getData(b);
        EditGridLayout layoutA = DataBag.getInstance().getData(a);
        Matrix B = layoutB.getEncsMatrix();
        Matrix A = layoutA.getEncsMatrix();

        try {
            Matrix C;
            if (op > -1) {
                if (op == 0)
                    C = A.add(B);
                else if (op == 1)
                    C = A.add(B.scalarMult(new ComplexForm(-1.0)));
                else if (op == 2)
                    C = A.mult(B);
                else {
                    C = A.leastSquares(B);
                    if (C.getError() > 1e-10)
                        Toast.makeText(DataBag.getInstance().getCurrView().getContext(), "No exact solution.\nLeast-squares approximation error: " + C.getError(), Toast.LENGTH_LONG).show();
                }
            } else{
                if (op == -1)
                    C = A.mult(B);
                else {
                    if (B.getElement(0,0).getReal() == (long)(1.0*B.getElement(0,0).getReal()))
                        C = A.power((int) (1.0 * B.getElement(0, 0).getReal()));
                    else
                        throw new IllegalArgumentException("Exponents must be integers");
                }
            }
            vg.removeView(layoutB);
            vg.removeView(layoutA);
            DataBag.getInstance().removeData(layoutB);
            DataBag.getInstance().removeData(layoutA);
            makeEditGrid(C, new Point(layoutB.getActualX(), layoutB.getActualY()));
        }catch (IllegalArgumentException e) {
            Toast.makeText(DataBag.getInstance().getCurrView().getContext(), colorize(e.getMessage()), Toast.LENGTH_SHORT).show();
        }
        invalidate();
        return true;
    }

    protected SpannableString colorize(String s){
        SpannableString text = new SpannableString(s);
        int i = 0;
        int j = 0;
        int z = 0;
        while(s.indexOf('A', i) != -1) {
            text.setSpan(new ForegroundColorSpan(Color.CYAN), s.indexOf('A', i), s.indexOf('A', i) + 1, 0);
            i = s.indexOf('A', i) + 1;
        }
        while(s.indexOf('B', j) != -1) {
            text.setSpan(new ForegroundColorSpan(Color.MAGENTA), s.indexOf('B', j), s.indexOf('B', j) + 1, 0);
            j = s.indexOf('B', j) + 1;
        }
        if (s.contains("x") && !s.contains("matrix"))
            text.setSpan(new ForegroundColorSpan(Color.rgb(35, 188, 196)), s.indexOf('x'), s.indexOf('x') + 1, 0);
        if (s.contains("n*A"))
            text.setSpan(new ForegroundColorSpan(Color.rgb(93, 204, 115)), s.indexOf('n'), s.indexOf('n')+1,0);
        return text;
    }

    protected void reveal(int i){
        if (i == 0)
            arithVis = View.VISIBLE;
        else if (i == 1)
            scalarVis = View.VISIBLE;
        else
            specialVis = View.VISIBLE;
        for (int q = 4*i; q < 4*i+4; q++) {
            if (q != 4 && q != 7 && q != 8 && q != 11)
                myButs[q].setVisibility(View.VISIBLE);
        }
    }

    protected void hide(){
        arithVis = View.GONE;
        scalarVis = View.GONE;
        specialVis = View.GONE;
        for (int i = 0; i < myButs.length; i++)
            myButs[i].setVisibility(View.GONE);
    }

    protected void arithButtons(final int a, final int b){
        shouldUpdate = true;
        DataBag.getInstance().queueOp(a, b);
        DataBag.getInstance().setArithOp(true);
        DataBag.getInstance().getData(a).switchBorderColor(Color.CYAN);
        DataBag.getInstance().getData(b).switchBorderColor(Color.MAGENTA);
        reveal(0);
        for (int i = 0; i < 4; i++){
            final int opCode = i;
            myButs[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                    DataBag.getInstance().setArithOp(false);
                    arithmetic(opCode, a, b);
                }
            });

        }
        invalidate();
    }

    protected void scalarButtons(final int a, final int b){
        DataBag.getInstance().getData(a).switchBorderColor(Color.CYAN);
        DataBag.getInstance().getData(b).switchBorderColor(Color.rgb(93,204,115));
        Spanned expText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            expText = Html.fromHtml("A<sup>n</sup>", Html.FROM_HTML_MODE_LEGACY);
        else
            expText = Html.fromHtml("A<sup>n</sup>");
        SpannableString sr = new SpannableString(expText);
        sr.setSpan(new ForegroundColorSpan(Color.CYAN), 0, 1, 0);
        sr.setSpan(new ForegroundColorSpan(Color.rgb(93, 204, 115)), 1, 2, 0);
        myButs[9].setText(colorize("n*A"));
        myButs[10].setText(sr);
        reveal(2);
        myButs[9].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                arithmetic(-1,a, b);
            }
        });
        myButs[10].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                arithmetic(-2,a,b);
            }
        });
    }

    private void scalarQuestionaire(final Point top){
        reveal(1);
        myButs[5].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                makeEditGrid(new Scalar(), top);
            }
        });
        myButs[6].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                makeEditGrid(new Matrix(1, 1), top);
            }
        });
        invalidate();
    }

    protected void makeEditGrid(Matrix m, Point top){
        ViewGroup vg = (ViewGroup) this.getParent();
        EditGridLayout result = new EditGridLayout(DataBag.getInstance().getCurrView().getContext(), cellLength, top, m);
        vg.addView(result);
        DataBag.getInstance().addData(result);
        DataBag.getInstance().getCurrView().invalidate();
    }

    protected int getCellLength(){
        return this.cellLength;
    }

    protected int getNumRows(){
        return this.numRows;
    }

    protected int getNumColumns(){
        return this.numColumns;
    }
}
/*

        ((GradientDrawable)eigen.getBackground()).setStroke(5, Color.DKGRAY);
 */