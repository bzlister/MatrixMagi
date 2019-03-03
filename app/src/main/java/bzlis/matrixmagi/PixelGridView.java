package bzlis.matrixmagi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bzlis.matrixmagi.R;


public class PixelGridView extends View {
    Point[] corners = new Point[2];
    protected int numColumns, numRows, numCells, cellLength;
    private Paint blackPaint = new Paint();
    private Paint redPaint = new Paint();
    private int buttonWidth;
    private int spacing;
    protected boolean shouldUpdate = false;
    private Button[] myButs = new Button[11];
    private int scalarVis = View.GONE;
    private int arithVis = View.GONE;
    private int specialVis = View.GONE;
    protected TextView ques;
    protected ImageView lews;
    protected TextView eigen;
    protected TextView inv;
    protected TextView det;
    protected TextView vvv;
    protected TextView ttt;
    protected TextView mag;
    protected TextView current = new TextView(getContext());
    public int id;
    public static int count;


    public PixelGridView(Context context){
        super(context, null);
        current.setBackgroundResource(R.drawable.func_selector);
        id = count;
        count++;
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(5);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setNumCells(int numCells){
        this.numCells = numCells;
        calculateDimensions();
    }

    public void makeGlow(MotionEvent me){
        if (me.getRawX() < DataBag.getInstance().getCurrView().det.getX()) {
            if (!current.equals(mag)) {
                ((GradientDrawable) current.getBackground()).setStroke(0, Color.TRANSPARENT);
                ((GradientDrawable) mag.getBackground()).setStroke(10, Color.RED);
                current = mag;
            }
        } else if (me.getRawX() < DataBag.getInstance().getCurrView().inv.getX()) {
            if (!current.equals(det)) {
                ((GradientDrawable) current.getBackground()).setStroke(0, Color.TRANSPARENT);
                ((GradientDrawable) det.getBackground()).setStroke(10, Color.RED);
                current = det;
            }
        } else if (me.getRawX() < DataBag.getInstance().getCurrView().ttt.getX()) {
            if (!current.equals(inv)) {
                ((GradientDrawable) current.getBackground()).setStroke(0, Color.TRANSPARENT);
                ((GradientDrawable) inv.getBackground()).setStroke(10, Color.RED);
                current = inv;
            }
        } else if (me.getRawX() < DataBag.getInstance().getCurrView().eigen.getX()) {
            if (!current.equals(ttt)) {
                ((GradientDrawable) current.getBackground()).setStroke(0, Color.TRANSPARENT);
                ((GradientDrawable) ttt.getBackground()).setStroke(10, Color.RED);
                current = ttt;
            }
        } else if (me.getRawX() < DataBag.getInstance().getCurrView().vvv.getX()) {
            if (!current.equals(eigen)) {
                ((GradientDrawable) current.getBackground()).setStroke(0, Color.TRANSPARENT);
                ((GradientDrawable) eigen.getBackground()).setStroke(10, Color.RED);
                current = eigen;
            }
        }
        else if (me.getRawX() < DataBag.getInstance().getCurrView().lews.getX()) {
            if (!current.equals(vvv)) {
                ((GradientDrawable) current.getBackground()).setStroke(0, Color.TRANSPARENT);
                ((GradientDrawable) vvv.getBackground()).setStroke(10, Color.RED);
                current = vvv;
            }
        }
        else{
            makeDim();
        }
    }

    public void makeDim(){
        ((GradientDrawable)current.getBackground()).setStroke(0, Color.TRANSPARENT);
        current = new TextView(getContext());
        current.setBackgroundResource(R.drawable.func_selector);
    }

    protected void makeTrashCan(){
        LinearLayout bottomRow = new LinearLayout(getContext());
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)Math.round(1.5*cellLength)));
        bottomRow.setTranslationY(cellLength*(numRows-2));
        int bottomRowWidth = Math.round(getWidth()/7.5f);

        mag = new TextView(this.getContext());
        SpannableString sst = new SpannableString("\u2016x\u2016");
        sst.setSpan(new ForegroundColorSpan(Color.rgb(35, 188, 196)), 1, 2, 0);
        mag.setText(sst);
        mag.setBackgroundResource(R.drawable.func_selector);
        mag.setTextSize(35);
        mag.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mag.setVisibility(View.INVISIBLE);
        mag.setTextIsSelectable(false);
        bottomRow.addView(mag, new LinearLayout.LayoutParams(bottomRowWidth, cellLength));

        det = new TextView(this.getContext());
        SpannableString ss = new SpannableString("|A|");
        ss.setSpan(new ForegroundColorSpan(Color.rgb(35, 188, 196)), 1, 2, 0);
        det.setText(ss);
        det.setBackgroundResource(R.drawable.func_selector);
        det.setTextSize(35);
        det.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        det.setVisibility(View.INVISIBLE);
        det.setTextIsSelectable(false);
        bottomRow.addView(det, new LinearLayout.LayoutParams(bottomRowWidth, cellLength));

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
        inv.setBackgroundResource(R.drawable.func_selector);
        inv.setTextSize(35);
        inv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        inv.setVisibility(View.INVISIBLE);
        inv.setTextIsSelectable(false);
        bottomRow.addView(inv, new LinearLayout.LayoutParams(bottomRowWidth, cellLength));


        ttt = new TextView(this.getContext());
        Spanned tttText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            tttText = Html.fromHtml("A<sup>T</sup>", Html.FROM_HTML_MODE_LEGACY);
        else
            tttText = Html.fromHtml("A<sup>T</sup>");
        SpannableString srt = new SpannableString(tttText);
        srt.setSpan(new ForegroundColorSpan(Color.rgb(35, 188, 196)), 0, 1, 0);
        srt.setSpan(new RelativeSizeSpan(0.6f), 1, 2, 0);
        ttt.setText(srt);
        ttt.setBackgroundResource(R.drawable.func_selector);
        ttt.setTextSize(35);
        ttt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ttt.setVisibility(View.INVISIBLE);
        ttt.setTextIsSelectable(false);
        ttt.setVisibility(INVISIBLE);
        bottomRow.addView(ttt, new LinearLayout.LayoutParams(bottomRowWidth, cellLength));

        eigen = new TextView(this.getContext());
        eigen.setText("\u03bb");
        eigen.setBackgroundResource(R.drawable.func_selector);
        eigen.setTextSize(35);
        eigen.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        eigen.setTextIsSelectable(false);
        eigen.setVisibility(View.INVISIBLE);
        bottomRow.addView(eigen, new LinearLayout.LayoutParams(bottomRowWidth, cellLength));

        vvv = new TextView(this.getContext());
        vvv.setText("v");
        vvv.setBackgroundResource(R.drawable.func_selector);
        vvv.setTextSize(35);
        vvv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        vvv.setTextIsSelectable(false);
        vvv.setVisibility(INVISIBLE);
        bottomRow.addView(vvv, new LinearLayout.LayoutParams(bottomRowWidth, cellLength));

        lews = new ImageView(getContext());
        lews.setImageResource(R.mipmap.tcan);
        lews.setBackgroundColor(Color.TRANSPARENT);
        lews.setVisibility(View.INVISIBLE);
        bottomRow.addView(lews, new LinearLayout.LayoutParams(bottomRowWidth*2, ViewGroup.LayoutParams.MATCH_PARENT));


        ques = new TextView(this.getContext());
        ques.setText("?");
        ques.setBackground(null);
        ques.setTextSize(35);
        ques.setTextColor(Color.RED);
        ques.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ques.setVisibility(View.VISIBLE);
        ques.setGravity(Gravity.CENTER);
        ques.setTextIsSelectable(false);
        ques.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBag.getInstance().tutOut = true;
                DataBag.getInstance().hideBoard();
                DataBag.getInstance().getCurrView().hide();
                EditGridLayout.hideKeyboard();
                if (shouldUpdate) {
                    for (EditGridLayout layout : DataBag.getInstance().getData())
                        layout.switchBorderColor(-1);
                    shouldUpdate = false;
                }
                DataBag.getInstance().deletor.setVisibility(View.GONE);
                ImageView tuts = new ImageView(DataBag.getInstance().getCurrView().getContext());
                tuts.setLayoutParams(DataBag.getInstance().getCurrView().getLayoutParams());
                tuts.setImageResource(R.mipmap.tuts);
                tuts.setBackgroundColor(Color.rgb(250, 250, 250));
                tuts.setVisibility(VISIBLE);
                tuts.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((RelativeLayout)v.getParent()).removeView(v);
                        v.setVisibility(View.GONE);
                        ImageView tuts2 = new ImageView(DataBag.getInstance().getCurrView().getContext());
                        tuts2.setLayoutParams(DataBag.getInstance().getCurrView().getLayoutParams());
                        tuts2.setBackgroundColor(Color.rgb(250, 250, 250));
                        tuts2.setImageResource(R.mipmap.tut2);
                        tuts2.setVisibility(VISIBLE);
                        ((ViewGroup)DataBag.getInstance().getCurrView().getParent()).addView(tuts2);
                        tuts2.setOnClickListener(new OnClickListener(){
                            @Override
                            public  void onClick(View v){
                                DataBag.getInstance().tutOut = false;
                                ((RelativeLayout)v.getParent()).removeView(v);
                                v.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                ((ViewGroup)DataBag.getInstance().getCurrView().getParent()).addView(tuts);
            }
        });
        ((ViewGroup)this.getParent()).addView(ques, cellLength, cellLength);

        ((ViewGroup)this.getParent()).addView(bottomRow);

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
        buttonWidth = Math.round(2*getWidth()/13f);
        spacing = Math.round(getWidth()/13f);
        String[] text = new String[]{"A + B", "A - B", "AB", "AX = B", "", "scalar","1x1 matrix", "", "cA","",""};

        for (int i = 0; i < myButs.length; i++){
            myButs[i] = new Button(DataBag.getInstance().getCurrView().getContext());
            if (i < 4)
                myButs[i].setVisibility(arithVis);
            else if (i == 5 || i == 6)
                myButs[i].setVisibility(scalarVis);
            else if (i == 8 || i == 9)
                myButs[i].setVisibility(specialVis);
            else
                myButs[i].setVisibility(GONE);
            myButs[i].setBackgroundResource(R.drawable.tags_rounded_corners);
            ((GradientDrawable)myButs[i].getBackground()).setColor(Color.rgb(240, 240, 240));
            ((GradientDrawable)myButs[i].getBackground()).setStroke(0, Color.rgb(240, 240, 240));
            myButs[i].setAllCaps(false);
            myButs[i].setTextColor(Color.GRAY);
            myButs[i].setText(colorize(text[i]));
            myButs[i].setTextSize(18);
            if (i < 8)
                myButs[i].setTranslationX((i%4)*buttonWidth+((i%4)+1)*spacing);
            else
                myButs[i].setTranslationX(((i-8)%3)*(4f/3)*buttonWidth+(((i-8)%3)+1)*(4f/3)*spacing);
            myButs[i].setTranslationY(cellLength*(numRows-2));
            ((ViewGroup)this.getParent()).addView(myButs[i]);
            myButs[i].setLayoutParams(new RelativeLayout.LayoutParams(buttonWidth, cellLength));
        }
        makeTrashCan();
        if (DataBag.getInstance().getArithOp())
            arithButtons(DataBag.getInstance().getA(), DataBag.getInstance().getB());
        DataBag.getInstance().getCurrView().invalidate();
        DataBag.getInstance().snapToGrid();
        DataBag.getInstance().makeBoard();
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
        }
        hide();
        makeDim();
        EditGridLayout.hideKeyboard();
        DataBag.getInstance().deletor.setVisibility(View.GONE);
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
                    int matCols = Math.round(Math.abs(corners[0].x - corners[1].x) / (1f*cellLength));
                    int matRows = Math.round(Math.abs(corners[0].y - corners[1].y) / (1f*cellLength));
                    Point top = new Point(Math.min(corners[0].x, corners[1].x), Math.min(corners[0].y, corners[1].y));
                    if (matCols == 1 && matRows == 1)
                        scalarQuestionaire(top);
                    else
                        makeEditGrid(new Matrix(matRows, matCols), top);
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
                else if (op == -2){
                    C = new Scalar(ComplexForm.div(new ComplexForm(1), B.getElement(0,0))).mult(A);
                }
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
        if (s.contains("\u2016x\u2016"))
            text.setSpan(new ForegroundColorSpan(Color.rgb(35, 188, 196)), s.indexOf('x'), s.indexOf('x') + 1, 0);
        if (s.contains("cA"))
            text.setSpan(new ForegroundColorSpan(Color.rgb(93, 204, 115)), s.indexOf('c'), s.indexOf('c')+1,0);
        return text;
    }

    protected void reveal(int i){
        makeDim();
        if (i == 0)
            arithVis = View.VISIBLE;
        else if (i == 1)
            scalarVis = View.VISIBLE;
        else
            specialVis = View.VISIBLE;
        if (i != 2) {
            for (int q = 4 * i; q < 4 * i + 4; q++) {
                if (q != 4 && q != 7)
                    myButs[q].setVisibility(View.VISIBLE);
            }
        }
        else{
            for (int q = 8; q < 11; q++)
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
            expText = Html.fromHtml("A<sup>c</sup>", Html.FROM_HTML_MODE_LEGACY);
        else
            expText = Html.fromHtml("A<sup>c</sup>");
        SpannableString sr = new SpannableString(expText);
        sr.setSpan(new ForegroundColorSpan(Color.CYAN), 0, 1, 0);
        sr.setSpan(new ForegroundColorSpan(Color.rgb(93, 204, 115)), 1, 2, 0);

        Spanned etest = Html.fromHtml("A\u00f7c");
        SpannableString test = new SpannableString(etest);
        test.setSpan(new ForegroundColorSpan(Color.CYAN), 0, 1, 0);
        test.setSpan(new ForegroundColorSpan(Color.rgb(93, 204, 115)), 2, 3, 0);



        myButs[8].setText(colorize("cA"));
        myButs[9].setText(test);
        myButs[10].setText(sr);
        reveal(2);
        myButs[8].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                arithmetic(-1,a, b);
            }
        });
        myButs[9].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                arithmetic(-2,a, b);
            }
        });
        myButs[10].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                arithmetic(-3,a,b);
            }
        });
    }

    private void scalarQuestionaire(final Point top){
        DataBag.getInstance().hideBoard();
        DataBag.getInstance().getCurrView().hide();
        EditGridLayout.hideKeyboard();
        if (shouldUpdate) {
            for (EditGridLayout layout : DataBag.getInstance().getData())
                layout.switchBorderColor(-1);
            shouldUpdate = false;
        }
        DataBag.getInstance().deletor.setVisibility(View.GONE);
        final LinearLayout scalarQ = new LinearLayout(getContext());
        scalarQ.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams rlparam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlparam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        scalarQ.setLayoutParams(rlparam);
        TextView tv = new TextView(getContext());
        scalarQ.setBackgroundResource(R.drawable.button_light);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setAllCaps(false);
        tv.setText("Make a");
        scalarQ.addView(tv);
        LinearLayout buttonRow = new LinearLayout(getContext());
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        final Button scalar = new Button(getContext());
        scalar.setTextColor(Color.GRAY);
        scalar.setBackground(null);
        scalar.setAllCaps(false);
        scalar.setText("Scalar");
        Button mat1x1 = new Button(getContext());
        SpannableString sst = new SpannableString("1x1 matrix");
        sst.setSpan(new ForegroundColorSpan(Color.rgb(35, 188, 196)), 4, 10, 0);
        mat1x1.setBackground(null);
        mat1x1.setAllCaps(false);
        mat1x1.setText(sst);
        scalar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scalarQ.setVisibility(View.GONE);
                makeEditGrid(new Scalar(), top);
            }
        });
        mat1x1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                scalarQ.setVisibility(View.GONE);
                makeEditGrid(new Matrix(1, 1), top);
            }
        });
        buttonRow.addView(scalar);
        buttonRow.addView(mat1x1);
        scalarQ.addView(buttonRow);
        ((ViewGroup)this.getParent()).addView(scalarQ);
        invalidate();
    }

    protected void makeEditGrid(Matrix m, Point top){
        ViewGroup vg = (ViewGroup) this.getParent();
        EditGridLayout result = new EditGridLayout(DataBag.getInstance().getCurrView().getContext(), cellLength, top, m);
        vg.addView(result);
        DataBag.getInstance().addData(result);
        DataBag.getInstance().getCurrView().invalidate();
    }
}