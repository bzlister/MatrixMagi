package bzlis.matrixmagi;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bzlis.matrixmagi.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Iterator;


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
        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (WorkerFragment) fm.findFragmentByTag(TAG_WORKER_FRAGMENT);
        if (mWorkerFragment == null){
            DataBag.getInstance().read(this);
            DataBag.getInstance().write(this, (DataBag.getInstance().numUses >= 0) ? DataBag.getInstance().numUses+1 : -1);
        }
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
        adView.setAdUnitId("ca-app-pub-2890801541122304/3308955741");
        frame.addView(adView);
        DataBag.getInstance().setAdView(adView);
        adView.bringToFront();


        final LinearLayout deleteAll = new LinearLayout(this);
        deleteAll.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams rlparam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlparam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        deleteAll.setLayoutParams(rlparam);
        TextView tv = new TextView(this);
        deleteAll.setBackgroundResource(R.drawable.button_light);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setAllCaps(false);
        tv.setText(getResources().getString(R.string.delAll));
        deleteAll.addView(tv);
        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        Button yes = new Button(this);
        yes.setTextColor(Color.rgb(35, 188, 196));
        yes.setBackground(null);
        yes.setAllCaps(false);
        yes.setText(getResources().getString(R.string.yes));
        Button no = new Button(this);
        no.setTextColor(Color.rgb(35, 188, 196));
        no.setBackground(null);
        no.setAllCaps(false);
        no.setText(getResources().getString(R.string.no));
        yes.setOnClickListener(new View.OnClickListener() {
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
                deleteAll.setVisibility(View.GONE);
            }
        });
        no.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DataBag.getInstance().deltut = false;
                deleteAll.setVisibility(View.GONE);
            }
        });
        buttonRow.addView(yes);
        buttonRow.addView(no);
        deleteAll.addView(buttonRow);
        deleteAll.setVisibility(View.GONE);
        frame.addView(deleteAll);

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
                    if ((DataBag.getInstance().getData().size() > 0) && !DataBag.getInstance().tutOut){
                        deleteAll.bringToFront();
                        DataBag.getInstance().getCurrView().hide();
                        DataBag.getInstance().getCurrBoard().hideBoard();
                        if (DataBag.getInstance().getCurrView().shouldUpdate) {
                            for (EditGridLayout layout : DataBag.getInstance().getData())
                                layout.switchBorderColor(-1);
                            DataBag.getInstance().getCurrView().shouldUpdate = false;
                        }
                        deleteAll.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (NullPointerException n){}
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
}