package com.example.bzlis.matrixmagi;

import android.os.Bundle;
import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;

public class WorkerFragment extends android.app.Fragment {

    // data object we want to retain
    private HashSet<EditGridLayout> editList = new HashSet<EditGridLayout>();

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void addData(EditGridLayout edit) {
        if (editList.contains(edit))
            removeData(edit);
        editList.add(edit);
    }

    public void removeData(EditGridLayout edit){
        Iterator<EditGridLayout> itr = editList.iterator();
        while (itr.hasNext()){
            if (itr.next().equals(edit)) {
                itr.remove();
                //edit.removeAllViews();
                break;
            }
        }
    }

    public boolean containsNullElements(){
        Iterator<EditGridLayout> itr = editList.iterator();
        boolean retVal = false;
        while (itr.hasNext()){
            if (itr.next() == null){
                retVal = true;
            }
        }
        return retVal;
    }

    public int getSize(){
        return editList.size();
    }

    public EditGridLayout removeMostRecent(){
        EditGridLayout toBeRemoved = null;
        int mostRecent = -1;
        for (EditGridLayout edit : editList){
            if (edit.getSecret() > mostRecent){
                mostRecent = edit.getSecret();
                toBeRemoved = edit;
            }
        }
        removeData(toBeRemoved);
        return toBeRemoved;
    }

    public HashSet<EditGridLayout> getData() {
        return editList;
    }

    public boolean isOccupied(int x0, int y0, int x1, int y1, int secret){
        boolean occupied = false;
        for (EditGridLayout edit : this.editList){
            if (edit.getSecret() != secret) {
                int x2 = edit.getActualX();
                int y2 = edit.getActualY();
                int x3 = edit.getActualX() + edit.getNumCols() * edit.getCellLength();
                int y3 = edit.getActualY() + edit.getNumRows() * edit.getCellLength();
                if (!((y1 < y2) || (x1 < x2) || (y3 < y0) || (x3 < x0))) {
                    if (!((y1 == y2) || (x1 == x2) || (y0 == y3) || (x0 == x3))) {
                        occupied = true;
                        break;
                    }
                }
            }
        }
        return occupied;
    }
}