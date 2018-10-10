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
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.Random;

public class PixelGridView extends View {
    Point[] corners = new Point[2];
    private Context context;
    public int numColumns, numRows, numCells;
    private int cellLength;
    private Paint blackPaint = new Paint();
    private Paint redPaint = new Paint();
    private WorkerFragment workerFragment;
    private int buttonColor;


    public PixelGridView(Context context, WorkerFragment workerFragment){
        super(context, null);
        this.context = context;
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(5);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.workerFragment = workerFragment;
        buttonColor = Color.rgb(209, 153, 82);
    }

    public void setNumCells(int numCells){
        this.numCells = numCells;
        calculateDimensions();
    }

    protected void setButtons(final int a, final int b){
        ViewGroup vg = (ViewGroup) this.getParent();
        int buttonWidth = Math.round(getWidth()/5.5f);
        int spacing = Math.round(getWidth()/22f);

        final Button add = new Button(this.getContext());
        add.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)add.getBackground()).setColor(buttonColor);
        ((GradientDrawable)add.getBackground()).setStroke(0, buttonColor);
        add.setText("+");
        add.setTextColor(Color.WHITE);
        add.setTranslationX(buttonWidth);
        add.setTranslationY(cellLength*(numRows-2));

        final Button mult = new Button(this.getContext());
        mult.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)mult.getBackground()).setColor(buttonColor);
        ((GradientDrawable)mult.getBackground()).setStroke(0, buttonColor);
        mult.setText("X");
        mult.setTextColor(Color.WHITE);
        mult.setTranslationX(2*buttonWidth+spacing);
        mult.setTranslationY(cellLength*(numRows-2));

        final Button back = new Button(this.getContext());
        back.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)back.getBackground()).setColor(buttonColor);
        ((GradientDrawable)back.getBackground()).setStroke(0, buttonColor);
        back.setText("<-");
        back.setTextColor(Color.WHITE);
        back.setTranslationX(3*buttonWidth+2*spacing);
        back.setTranslationY(cellLength*(numRows-2));
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                arithmetic(0, a, b);
                add.setVisibility(View.GONE);
                mult.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
        });
        mult.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                arithmetic(1, a, b);
                mult.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                add.setVisibility(View.GONE);
                mult.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
        });
        vg.addView(add, buttonWidth, cellLength+spacing);
       // vg.bringChildToFront(add);
        vg.addView(mult, buttonWidth, cellLength+spacing);
       // vg.bringChildToFront(mult);
        vg.addView(back, buttonWidth, cellLength+spacing);
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
                //vg.bringChildToFront(editGrid);
            }
            corners[0] = null;
            corners[1] = null;
        }
        else
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
            Matrix C;
            if (op == 0)
                C = A.add(B);
            else
                C = A.mult(B);
            Point p = new Point((int)(cellLength*Math.round(0.5*(getWidth()-cellLength*C.getNumCols())/cellLength)), (int)(cellLength*Math.round(0.25*getHeight()/cellLength)));
            makeEditGrid(C, p);
               // vg.bringChildToFront(result);
        } catch (IllegalArgumentException e){
            Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        invalidate();
        return true;
    }

    private void scalarQuestionaire(final Point top){
        ViewGroup vg = (ViewGroup) this.getParent();
        int buttonWidth = Math.round(getWidth()/5.5f);
        int spacing = Math.round(getWidth()/22f);

        final Button scalar = new Button(this.getContext());
        scalar.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)scalar.getBackground()).setColor(buttonColor);
        ((GradientDrawable)scalar.getBackground()).setStroke(0, buttonColor);
        scalar.setText("scalar");
        scalar.setTextColor(Color.WHITE);
        scalar.setTranslationX(buttonWidth);
        scalar.setTranslationY(cellLength*(numRows-2));


        final Button matrix = new Button(this.getContext());
        matrix.setBackgroundResource(R.drawable.tags_rounded_corners);
        ((GradientDrawable)matrix.getBackground()).setColor(buttonColor);
        ((GradientDrawable)matrix.getBackground()).setStroke(0, buttonColor);
        matrix.setText("matrix");
        matrix.setTextColor(Color.WHITE);
        matrix.setTranslationX(3*buttonWidth+2*spacing);
        matrix.setTranslationY(cellLength*(numRows-2));

        scalar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                matrix.setVisibility(View.GONE);
                scalar.setVisibility(View.GONE);
                makeEditGrid(new Scalar(), top);
            }
        });
        matrix.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                matrix.setVisibility(View.GONE);
                scalar.setVisibility(View.GONE);
                makeEditGrid(new Matrix(1, 1), top);
            }
        });

        vg.addView(scalar);
        vg.addView(matrix);
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