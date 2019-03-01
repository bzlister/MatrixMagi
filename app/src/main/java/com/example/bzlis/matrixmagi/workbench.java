package com.example.bzlis.matrixmagi;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private int numCells = 180;
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
        DataBag.getInstance().setVibrator((Vibrator)this.getSystemService(VIBRATOR_SERVICE));

        MobileAds.initialize(this, "ca-app-pub-2890801541122304~4346705243");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        adView.setId(View.generateViewId());
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        frame.addView(adView);
        DataBag.getInstance().setAdView(adView);
        adView.bringToFront();

        Button deleteAll = new Button(this);
        RelativeLayout.LayoutParams rlparam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlparam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        deleteAll.setLayoutParams(rlparam);
        deleteAll.setAllCaps(false);
        deleteAll.setText("Delete all?");
        deleteAll.setVisibility(View.GONE);
        frame.addView(deleteAll);
        DataBag.getInstance().deletor = deleteAll;
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBag.getInstance().deltut = false;
                Iterator<EditGridLayout> it = DataBag.getInstance().getData().iterator();
                while (it.hasNext()) {
                    ((ViewGroup) DataBag.getInstance().getCurrView().getParent()).removeView(it.next());
                    it.remove();
                }
                DataBag.getInstance().getCurrView().hide();
                DataBag.getInstance().getCurrView().invalidate();
                DataBag.getInstance().deletor.setVisibility(View.GONE);
            }
        });

        if (mWorkerFragment == null) {
            mWorkerFragment = new WorkerFragment();
            fm.beginTransaction().add(mWorkerFragment, TAG_WORKER_FRAGMENT).commit();
            DataBag.getInstance().adLoader(new AdRequest.Builder().build());
        }
        else {
            DataBag.getInstance().cleanData(frame);
            DataBag.getInstance().adLoader(new AdRequest.Builder().build());
        }
        setContentView(frame);
        try {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();
            mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
                @Override
                public void onShake(int count) {
                    if (DataBag.getInstance().getData().size() > 0) {
                        DataBag.getInstance().getCurrView().hide();
                        if (DataBag.getInstance().getCurrView().shouldUpdate) {
                            for (EditGridLayout layout : DataBag.getInstance().getData())
                                layout.switchBorderColor(-1);
                            DataBag.getInstance().getCurrView().shouldUpdate = false;
                        }
                        DataBag.getInstance().deletor.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (NullPointerException n){}
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