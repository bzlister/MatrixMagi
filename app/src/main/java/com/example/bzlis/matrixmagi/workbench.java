package com.example.bzlis.matrixmagi;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.HashSet;

public class workbench extends AppCompatActivity {

    private int numCells = 200;
    private static final String TAG_WORKER_FRAGMENT = "WorkerFragment";
    private WorkerFragment mWorkerFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (WorkerFragment) fm.findFragmentByTag(TAG_WORKER_FRAGMENT);
        // create the fragment and data the first time
        if (mWorkerFragment == null) {
            mWorkerFragment = new WorkerFragment();
            fm.beginTransaction().add(mWorkerFragment, TAG_WORKER_FRAGMENT).commit();
        }
        RelativeLayout frame = new RelativeLayout(this);

       // frame.requestFocus();
        PixelGridView pr = new PixelGridView(this, mWorkerFragment);
        frame.addView(pr);
        pr.setNumCells(numCells);
        if ((mWorkerFragment.getData() != null) && (mWorkerFragment.getData().size() != 0)) {
           HashSet<EditGridLayout> editList = mWorkerFragment.getData();
            for (EditGridLayout edit : editList) {
                try {
                    ((ViewGroup) edit.getParent()).removeView(edit);
                } catch (NullPointerException e){}
                try {
                    //is this still meaningful?
                    if (edit.removed)
                        mWorkerFragment.removeData(edit);
                } catch (NullPointerException e) {}
                frame.addView(edit);
            }
        }
        setContentView(frame);
    }
}











    /*\
     implements EditNameDialog.OnGetFromUserClickListener
    HashMap<Integer, Matrix> made = new HashMap();

    @Override
    public void onCreate(Bundle savedInstanceState) {

       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workbench);
        PixelGridView pixelGrid = new PixelGridView(this);
        pixelGrid.setNumCells(300);
        pixelGrid.fm = getSupportFragmentManager();
        setContentView(pixelGrid);
    }

    public void getFromUser(String message) {
        sendMessage(message);
    }

    public void sendMessage(String message){
        String[] data = message.split(",");
        if (!made.containsKey(data[0]))
            made.put(Integer.parseInt(data[0]), new Matrix(message));
        made.put(Integer.parseInt(data[0]), made.get(Integer.parseInt(data[0])).setElement(message));
    }
    */
