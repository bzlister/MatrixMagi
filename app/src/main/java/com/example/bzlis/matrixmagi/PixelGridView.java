package com.example.bzlis.matrixmagi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class PixelGridView extends View {
    Point[] corners = new Point[2];
    public int numColumns, numRows, numCells;
    private int cellLength;
    private Paint blackPaint = new Paint();
    private Paint redPaint = new Paint();
    private WorkerFragment workerFragment;
    private int buttonColor;
    private int buttonWidth;
    private boolean shouldUpdate = false;
    private Button[] myButs = new Button[4];
    private static int scalarVis = View.GONE;
    private static int arithVis = View.GONE;

    public TextView lews;


    public PixelGridView(Context context, WorkerFragment workerFragment){
        super(context, null);
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(5);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.workerFragment = workerFragment;
        buttonColor = Color.rgb(255, 153, 0);
    }

    public void setNumCells(int numCells){
        this.numCells = numCells;
        calculateDimensions();
    }

    protected void makeTrashCan(){
        /*
        ImageView viewz = new ImageView(this.getContext());
        viewz.setLayoutParams(new RelativeLayout.LayoutParams(cellLength*2, cellLength*2));
        viewz.setTranslationX(cellLength*(numColumns-2));
        viewz.setTranslationY(cellLength*(numRows-2));
        viewz.setImageResource(R.mipmap.ic_launcher);
        ((ViewGroup)this.getParent()).addView(viewz);
        */
        lews = new TextView(this.getContext());
        lews.setText("X");
        lews.setTextColor(Color.RED);
        lews.setTextSize(50);
        lews.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        lews.setTranslationX(cellLength*(numColumns-2));
        lews.setTranslationY(Math.round(cellLength*(numRows-2.2)));
        lews.setBackground(null);
        lews.setTextIsSelectable(false);
        lews.setVisibility(INVISIBLE);
        ((ViewGroup)this.getParent()).addView(lews, cellLength*2, cellLength*2);
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
        numRows = (int)Math.round(getHeight()/(0.0 + cellLength));
       // Log.i("srx", (myButs[0] == null) + ", " + (myButs[0].getVisibility()) + ", " + myButs[0].getWidth() + ", " + myButs[0].getHeight()+ ", (" + (myButs[0].getX()) + ","+(myButs[0].getY()) +")");
        for (EditGridLayout edit : workerFragment.getData())
            edit.setDad(this);
        buttonWidth = Math.round(getWidth()/5f);
        String[] text = new String[]{"+","X","scalar","1x1 matrix"};
        for (int i = 0; i < 4; i++){
            myButs[i] = new Button(this.getContext());
            if (i < 2)
                myButs[i].setVisibility(arithVis);
            else
                myButs[i].setVisibility(scalarVis);
            myButs[i].setBackgroundResource(R.drawable.tags_rounded_corners);
            ((GradientDrawable)myButs[i].getBackground()).setColor(buttonColor);
            ((GradientDrawable)myButs[i].getBackground()).setStroke(0, buttonColor);
            myButs[i].setText(text[i]);
            myButs[i].setTextColor(Color.rgb(242, 244, 246));
            myButs[i].setTranslationX((2*(i%2)+1)*buttonWidth);
            myButs[i].setTranslationY(cellLength*(numRows-2));
            //((ViewGroup)this.getParent()).removeView(myButs[i]);
            ((ViewGroup)this.getParent()).addView(myButs[i]);
            myButs[i].setLayoutParams(new RelativeLayout.LayoutParams(buttonWidth, cellLength));
        }
        Log.i("eara", ((RelativeLayout)this.getParent()).getChildCount()+"");
        makeTrashCan();
        /*
        for (EditGridLayout layout : workerFragment.getData())
            layout.switchBorderColor(-1);
            */
        invalidate();
    }

    public void makeToast(){
        Toast.makeText(this.getContext(), "Stanky old toast", Toast.LENGTH_LONG).show();
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
        /*
        if (shouldUpdate) {
            for (EditGridLayout layout : workerFragment.getData())
                layout.switchBorderColor(-1);
            shouldUpdate = false;
            invalidate();
        }
        */
        hide();
        int x = cellLength * Math.round(event.getX() / cellLength);
        int y = cellLength * Math.round(event.getY() / cellLength);
        if (corners[0] == null) {
            corners[0] = new Point(x, y);
            corners[1] = corners[0];
        } else
            corners[1] = new Point(x, y);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if ((corners[0].y - corners[1].y != 0) && (corners[0].x - corners[1].x != 0) && (workerFragment.isOccupied(Math.min(corners[0].x, corners[1].x), Math.min(corners[0].y, corners[1].y), Math.max(corners[0].x, corners[1].x), Math.max(corners[0].y, corners[1].y), -1, false) < 0)) {
                int matCols = Math.round(Math.abs(corners[0].x - corners[1].x) / cellLength);
                int matRows = Math.round(Math.abs(corners[0].y - corners[1].y) / cellLength);
                Point top = new Point(Math.min(corners[0].x, corners[1].x), Math.min(corners[0].y, corners[1].y));
                if (matCols == 1 && matRows == 1)
                    scalarQuestionaire(top);
                else
                    makeEditGrid(new Matrix(matRows, matCols), top);
            }
            corners[0] = null;
            corners[1] = null;
        }
        else
            invalidate();
        return true;
    }




    private boolean arithmetic(int op, int a, int b){
        Log.i("blam", a+", " + b);
        ViewGroup vg = (ViewGroup) this.getParent();
        EditGridLayout layoutB = workerFragment.getData(b);
        EditGridLayout layoutA = workerFragment.getData(a);
        Matrix B = layoutB.getEncsMatrix();
        Matrix A = layoutA.getEncsMatrix();

        try{
            Matrix C;
            if (op == 0)
                C = A.add(B);
            else
                C = A.mult(B);
            vg.removeView(layoutB);
            vg.removeView(layoutA);
            workerFragment.removeData(layoutB);
            workerFragment.removeData(layoutA);
            //Point p = new Point((int)(cellLength*Math.round(0.5*(getWidth()-cellLength*C.getNumCols())/cellLength)), (int)(cellLength*Math.round(0.25*getHeight()/cellLength)));
            makeEditGrid(C, new Point(layoutB.getActualX(), layoutB.getActualY()));
               // vg.bringChildToFront(result);
        } catch (IllegalArgumentException e){
            Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        invalidate();
        return true;
    }

    protected void reveal(int i){
        if (i == 0)
            arithVis = View.VISIBLE;
        else
            scalarVis = View.VISIBLE;
        myButs[2*(i%2)].setVisibility(View.VISIBLE);
        myButs[2*(i%2)+1].setVisibility(View.VISIBLE);
    }

    protected void hide(){
        arithVis = View.GONE;
        scalarVis = View.GONE;
        for (int i = 0; i < 4; i++)
            myButs[i].setVisibility(View.GONE);
    }

    protected void arithButtons(final int a, final int b){
      //  shouldUpdate = true;
      //  workerFragment.getData(a).switchBorderColor(Color.CYAN);
       // workerFragment.getData(b).switchBorderColor(Color.MAGENTA);
    //    for (EditGridLayout edit : workerFragment.getData())
         //   edit.keyboardLock(true);
        reveal(0);
        for (int i = 0; i < 2; i++){
            final int opCode = i;
            myButs[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                    arithmetic(opCode, a, b);
                }
            });

        }
        invalidate();
    //    Log.i("55t", "V"+myButs[0].getVisibility() + ": " + myButs[0].getX() + ", " + myButs[0].getY() +", "+ myButs[0].getWidth() + ", " + myButs[0].getHeight());
    }

    private void scalarQuestionaire(final Point top){
        reveal(1);
        for (int i = 2; i < 4; i++){
            final int opCode = i;
            myButs[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                    if (opCode == 2)
                        makeEditGrid(new Scalar(), top);
                    else
                        makeEditGrid(new Matrix(1, 1), top);
                }
            });
        }
        invalidate();
      /*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            scalar.setText(Html.fromHtml("X<sup>2</sup>", Html.FROM_HTML_MODE_LEGACY));
        else
            scalar.setText(Html.fromHtml("X<sup>2</sup>"));
            */
    }

    private void makeEditGrid(Matrix m, Point top){
        ViewGroup vg = (ViewGroup) this.getParent();
        EditGridLayout result = new EditGridLayout(this.getContext(), cellLength, workerFragment, top, m, this);
        vg.addView(result);
        workerFragment.addData(result);
        invalidate();
    }
}