package com.example.bzlis.matrixmagi;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Iterator;

import static android.view.View.VISIBLE;

public class workbench extends AppCompatActivity {

    private int numCells = 160;
    private static final String TAG_WORKER_FRAGMENT = "WorkerFragment";
    private WorkerFragment mWorkerFragment;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (WorkerFragment) fm.findFragmentByTag(TAG_WORKER_FRAGMENT);
        // create the fragment and data the first time

        final RelativeLayout frame = new RelativeLayout(this);

        PixelGridView pr = new PixelGridView(this);
        frame.addView(pr);
        pr.setNumCells(numCells);
        DataBag.getInstance().setCurrView(pr);


        MobileAds.initialize(this, "ca-app-pub-2890801541122304~4346705243");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        adView.setId(View.generateViewId());
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        frame.addView(adView);
        DataBag.getInstance().setAdView(adView);
        adView.bringToFront();

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
                    DataBag.getInstance().adLoader(new AdRequest.Builder().build());
                    return false;
                }
            });
            frame.addView(tuts);
        }
        else {
            DataBag.getInstance().cleanData(frame);
            DataBag.getInstance().adLoader(new AdRequest.Builder().build());
        }
        setContentView(frame);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                if (count >= 1){
                    DataBag.getInstance().deltut = false;
                    Iterator<EditGridLayout> it = DataBag.getInstance().getData().iterator();
                    while (it.hasNext()){
                        ((ViewGroup) DataBag.getInstance().getCurrView().getParent()).removeView(it.next());
                        it.remove();
                    }
                    DataBag.getInstance().getCurrView().hide();
                    DataBag.getInstance().getCurrView().invalidate();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}