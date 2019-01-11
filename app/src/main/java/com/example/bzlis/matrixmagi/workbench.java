package com.example.bzlis.matrixmagi;

import android.app.FragmentManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashSet;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.VISIBLE;

public class workbench extends AppCompatActivity {

    private int numCells = 160;
    private static final String TAG_WORKER_FRAGMENT = "WorkerFragment";
    private WorkerFragment mWorkerFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (WorkerFragment) fm.findFragmentByTag(TAG_WORKER_FRAGMENT);
        // create the fragment and data the first time
        RelativeLayout frame = new RelativeLayout(this);
        PixelGridView pr = new PixelGridView(this);
        frame.addView(pr);
        DataBag.getInstance().setCurrView(pr);
        pr.setNumCells(numCells);
        if (mWorkerFragment == null) {
            mWorkerFragment = new WorkerFragment();
            fm.beginTransaction().add(mWorkerFragment, TAG_WORKER_FRAGMENT).commit();
            ImageView tuts = new ImageView(this);
            tuts.setLayoutParams(pr.getLayoutParams());
            tuts.setImageResource(R.mipmap.tuts);
            tuts.setBackgroundColor(Color.WHITE);
            tuts.setVisibility(VISIBLE);
            tuts.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ((RelativeLayout)v.getParent()).removeView(v);
                    v.setVisibility(View.GONE);
                    return false;
                }
            });
            frame.addView(tuts);
        }
        else
            DataBag.getInstance().cleanData(frame);
        String s = "";
        for (EditGridLayout edit : DataBag.getInstance().getData())
            s = s + edit.getSecret() + ", ";
        Log.i("SecretChannel", s);
        setContentView(frame);
    }
}