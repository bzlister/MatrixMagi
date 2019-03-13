package bzlis.matrixmagi;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;

public class DataBag {

    private static DataBag instance = new DataBag();
    private HashSet<EditGridLayout> editList;
    private PixelGridView currView;
    private int A;
    private int B;
    private boolean arithOp;
    protected AdView adView;
    public boolean boardOut;
    public boolean deltut;
    public Vibrator vibes;
    public boolean tutOut;
    private Trueboard board;
    public int numUses;

    private DataBag(){
        editList = new HashSet<>();
        arithOp = false;
        boardOut = false;
        deltut = true;
        tutOut = false;
    }

    public static DataBag getInstance(){
        return instance;
    }

    public void addData(EditGridLayout edit){
        editList.add(edit);
    }

    public EditGridLayout getData(int secret) {
        EditGridLayout retVal = null;
        Iterator<EditGridLayout> itr = editList.iterator();
        while (itr.hasNext()) {
            if ((retVal = itr.next()).getSecret() == secret)
                break;
        }
        return retVal;
    }

    public void removeData(EditGridLayout edit){
        Iterator<EditGridLayout> itr = editList.iterator();
        while (itr.hasNext()){
            if (itr.next().equals(edit)) {
                itr.remove();
                break;
            }
        }
    }

    public void adLoader(AdRequest adRequest){
        adView.loadAd(adRequest);
    }

    public void setAdView(AdView adView){
        this.adView = adView;
    }

    public int isOccupied(int x0, int y0, int x1, int y1, int secret, boolean actual){
        int occupied = -1;
        for (EditGridLayout edit : this.editList){
            if (edit.getSecret() != secret) {
                float x2, y2, x3, y3;
                if (!actual) {
                    x2 = edit.getActualX();
                    y2 = edit.getActualY();
                    x3 = x2 + edit.getNumCols() * edit.getCellLength();
                    y3 = y2 + edit.getNumRows() * edit.getCellLength();
                } else {
                    x2 = edit.getX();
                    y2 = edit.getY();
                    y3 = y2 + edit.getCellLength()*(edit.getNumRows()+2*edit.getThickness());
                    x3 = x2 + edit.getCellLength()*(edit.getNumCols()+2*edit.getThickness());
                }
                if (!((y1 < y2) || (x1 < x2) || (y3 < y0) || (x3 < x0))) {
                    if (!((y1 == y2) || (x1 == x2) || (y0 == y3) || (x0 == x3))) {
                        occupied = actual ? edit.getSecret() : 1;
                        break;
                    }
                }
            }
        }
        return occupied;
    }

    public HashSet<EditGridLayout> getData() {
        return editList;
    }

    public void setCurrView(PixelGridView px){
        this.currView = px;
    }


    public PixelGridView getCurrView(){
        return currView;
    }

    public int getA(){
        return A;
    }

    public int getB(){
        return B;
    }

    public boolean getArithOp(){
        return arithOp;
    }

    public void setArithOp(boolean arithOp){
        this.arithOp = arithOp;
    }

    public void cleanData(RelativeLayout layout){
        for (EditGridLayout edit : editList){
            try {
                ((ViewGroup) edit.getParent()).removeView(edit);
                edit.invalidate();
            } catch (NullPointerException e) {}
            layout.addView(edit);
        }
    }

    public void queueOp(int a, int b){
        this.A = a;
        this.B = b;
    }

    public void snapToGrid(){
      for (EditGridLayout edit : editList) {
        edit.setX(currView.cellLength * Math.round((edit.getX() + currView.cellLength) / currView.cellLength) - currView.cellLength * edit.thick);
        edit.setY(currView.cellLength * Math.round((edit.getY() + currView.cellLength) / currView.cellLength) - currView.cellLength * edit.thick);
        }
    }


    public void setVibrator(Vibrator vibes){
        this.vibes = vibes;
    }

    public Trueboard getCurrBoard(){
        return board;
    }

    public void setCurrBoard(Trueboard board){
        this.board = board;
    }

    public void read(Context context){
        String line;
        try {
            InputStream stream = context.openFileInput("MatrixMagusUseCount.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            line = br.readLine().trim();
            numUses = Integer.valueOf(line);
            stream.close();
        } catch (FileNotFoundException e) {
            numUses = 0;
        } catch (IOException i){
            numUses = 0;
        }
    }

    public void write(Context context, int what){
        numUses = what;
        try{
            FileOutputStream fo = context.openFileOutput("MatrixMagusUseCount.txt", AppCompatActivity.MODE_PRIVATE);
            BufferedWriter bw  = new BufferedWriter(new OutputStreamWriter(fo));
            bw.write(Integer.toString(what));
            bw.close();
            fo.close();
        } catch (Exception e){
        }
    }
}
