package com.example.bzlis.matrixmagi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.util.Random;

public class PixelGridView extends View {
    Point[] corners = new Point[2];
    private Context context;
    private int numColumns, numRows, numCells;
    private int cellLength;
    private Paint blackPaint = new Paint();
    private Paint redPaint = new Paint();
    private WorkerFragment workerFragment;
    private int buttonColor;
    private int borderColor;


    public PixelGridView(Context context, WorkerFragment workerFragment){
        super(context, null);
        this.context = context;
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(5);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.workerFragment = workerFragment;
        int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.LTGRAY};
        buttonColor = colors[new Random().nextInt(colors.length)];
        borderColor = colors[new Random().nextInt(colors.length)];
        while (borderColor == buttonColor){
            borderColor = colors[new Random().nextInt(colors.length)];
        }

    }

    public void setNumCells(int numCells){
        this.numCells = numCells;
        calculateDimensions();
    }

    protected void setButtons(final int a, final int b){
        ViewGroup vg = (ViewGroup) this.getParent();
        final Button add = new Button(this.getContext());
        //add.setLayoutParams(new RelativeLayout.LayoutParams(2*cellLength, 2*cellLength));
        add.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)add.getBackground()).setColor(buttonColor);
        ((GradientDrawable)add.getBackground()).setStroke(0, buttonColor);
        add.setText("+");
        add.setTranslationX(cellLength*(numColumns/3f));
        add.setTranslationY(cellLength*(numRows/2f));
        final Button mult = new Button(this.getContext());
        //mult.setLayoutParams(new FrameLayout.LayoutParams(20*cellLength, 20*cellLength, Gravity.FILL));
        mult.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)mult.getBackground()).setColor(buttonColor);
        ((GradientDrawable)mult.getBackground()).setStroke(0, buttonColor);
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                arithmetic(0, a, b);
                add.setVisibility(View.GONE);
                mult.setVisibility(View.GONE);
            }
        });
        mult.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                arithmetic(1, a, b);
                mult.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
            }
        });
        mult.setText("X");
        mult.setTranslationX(cellLength*(2*numColumns/3f));
        mult.setTranslationY(cellLength*(numRows/2f));
        vg.addView(add, 2*cellLength, cellLength);
        vg.bringChildToFront(add);
        vg.addView(mult, 2*cellLength, cellLength);
        vg.bringChildToFront(mult);
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
        invalidate();
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

        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellLength, 0, i * cellLength, height, blackPaint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellLength, width, i * cellLength, blackPaint);
        }
    }



    public boolean onTouchEvent(MotionEvent event) {
        ViewGroup vg = (ViewGroup) this.getParent();
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
                    EditGridLayout editGrid = new EditGridLayout(this.getContext(), matRows, matCols, cellLength, workerFragment, top, null, borderColor, this);
                    workerFragment.addData(editGrid);
                    vg.addView(editGrid);
                    //vg.bringChildToFront(editGrid);
                }
                corners[0] = null;
                corners[1] = null;
            }
            invalidate();
    return true;
}


    private boolean arithmetic(int op, int a, int b){
        ViewGroup vg = (ViewGroup) this.getParent();
        EditGridLayout layoutB = workerFragment.getData(b);
        EditGridLayout layoutA = workerFragment.getData(a);
        Matrix B = layoutB.getEncsMatrix();
        Matrix A = layoutA.getEncsMatrix();
        vg.removeView(layoutB);
        vg.removeView(layoutA);
        workerFragment.removeData(layoutB);
        workerFragment.removeData(layoutA);
        try{
            Log.i("Matrix", B.toString());
            Log.i("Matrix", A.toString());
            Log.i("Matrix", "Num mats: " + workerFragment.getSize());
            Matrix C;
            if (op == 0)
                C = A.add(B);
            else
                C = A.mult(B);
            Point p = new Point((int)(cellLength*Math.round(0.5*(getWidth()-cellLength*C.getNumCols())/cellLength)), (int)(cellLength*Math.round(0.25*getHeight()/cellLength)));
            EditGridLayout result = new EditGridLayout(this.getContext(), C, cellLength, workerFragment, p, borderColor, this);
            vg.addView(result);
               // vg.bringChildToFront(result);
        } catch (IllegalArgumentException e){
            Toast.makeText(this.getContext(), "Incorrect matrix dimensions", Toast.LENGTH_SHORT).show();
        }
        invalidate();
        return true;
    }
}