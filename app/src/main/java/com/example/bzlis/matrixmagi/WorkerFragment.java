package com.example.bzlis.matrixmagi;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.HashSet;
import java.util.Iterator;

public class WorkerFragment extends android.app.Fragment {

    // data object we want to retain
    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }
}