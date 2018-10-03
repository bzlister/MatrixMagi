package com.example.bzlis.matrixmagi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Iterator;

import in.championswimmer.sfg.lib.SimpleFingerGestures;

public class PixelGridView extends View {
    Point[] corners = new Point[2];
    private Context context;
    private int numColumns, numRows, numCells;
    private int cellLength;
    private Paint blackPaint = new Paint();
    private Paint redPaint = new Paint();
    private Paint bluePaint = new Paint();
    private WorkerFragment workerFragment;

    public PixelGridView(Context context, WorkerFragment workerFragment){
        super(context, null);
        this.context = context;
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(5);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bluePaint.setStyle(Paint.Style.STROKE);
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStrokeWidth(6);
        this.workerFragment = workerFragment;
    }

    public void setNumCells(int numCells){
        this.numCells = numCells;
        calculateDimensions();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        if (numCells < 1) {
            return;
        }
        cellLength = (int)Math.round(Math.sqrt((getWidth()*getHeight()*1.0)/numCells));
        numColumns = (int)Math.round(getWidth()/(0.0 + cellLength));
        numRows = (int)Math.round(getHeight()/(0.0 + cellLength));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0xefefef);
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

        /*
        for (EditGridLayout edit : workerFragment.getData()){
            if (!edit.removed) {
                float x = edit.getX() + 0.25f*cellLength;
                float y = edit.getY() + 0.25f*cellLength;
                canvas.drawLine(x, y, x, y + edit.getNumRows() * cellLength, bluePaint);
                canvas.drawLine(x + (edit.getNumCols()-0.25f)*cellLength, y, x + (edit.getNumCols()-0.25f)*cellLength, y + edit.getNumRows() * cellLength, bluePaint);
            }
        }
*/
    }

    private boolean isOccupied(){
        int x0 = Math.min(corners[0].x, corners[1].x);
        int y0 = Math.min(corners[0].y, corners[1].y);
        int x1 = Math.max(corners[0].x, corners[1].x);
        int y1 = Math.max(corners[0].y, corners[1].y);
        boolean free = true;
        for (EditGridLayout edit : workerFragment.getData()){
            int x2 = (int)edit.getX();
            int y2 = (int)edit.getY();
            int x3 = (int)edit.getX()+edit.getNumCols()*cellLength;
            int y3 = (int)edit.getY()+edit.getNumRows()*cellLength;
            if (!((y1<=y2) || (x1<=x2) || (y3<=y0) || (x3<=x0))){
                free = false;
                break;
            }
        }
        return free;
    }

    public boolean onTouchEvent(MotionEvent event) {
        ViewGroup vg = (ViewGroup) this.getParent();
        if (workerFragment.getSize() < 2) {
            int x = cellLength * Math.round(event.getX() / cellLength);
            int y = cellLength * Math.round(event.getY() / cellLength);
            if (corners[0] == null) {
                corners[0] = new Point(x, y);
                corners[1] = corners[0];
            } else
                corners[1] = new Point(x, y);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if ((corners[0].y - corners[1].y != 0) && (corners[0].x - corners[1].x != 0) && (isOccupied())) {
                    int matCols = Math.round(Math.abs(corners[0].x - corners[1].x) / cellLength);
                    int matRows = Math.round(Math.abs(corners[0].y - corners[1].y) / cellLength);
                    Point top = new Point(Math.min(corners[0].x, corners[1].x), Math.min(corners[0].y, corners[1].y));
                    EditGridLayout editGrid = new EditGridLayout(this.getContext(), matRows, matCols, cellLength, workerFragment, top, null);
                    workerFragment.addData(editGrid);
                    vg.addView(editGrid);
                    vg.bringChildToFront(editGrid);
                }
                corners[0] = null;
                corners[1] = null;
            }
        }
        else{
            EditGridLayout layoutB = workerFragment.removeMostRecent();
            EditGridLayout layoutA = workerFragment.removeMostRecent();
            Matrix B = layoutB.getEncsMatrix();
            Matrix A = layoutA.getEncsMatrix();
            try{
                Matrix C = A.mult(B);
                Point p = new Point((int)(cellLength*Math.round(0.5*(getWidth()-cellLength*C.getNumCols())/cellLength)), (int)(cellLength*Math.round(0.25*getHeight()/cellLength)));
                EditGridLayout product = new EditGridLayout(this.getContext(), C, cellLength, workerFragment, p);
                //editGridLayouts.add(product);
                vg.addView(product);
                vg.bringChildToFront(product);
            } catch (IllegalArgumentException e){
                Toast.makeText(this.getContext(), "Incorrect matrix dimensions", Toast.LENGTH_SHORT).show();
            }
            //If not removed then only gets removed on screen change
            vg.removeView(layoutB);
            vg.removeView(layoutA);
        }
        invalidate();
    return true;
}
}