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

    public EditGridLayout getData(int secret){
        EditGridLayout retVal = null;
        Iterator<EditGridLayout> itr = editList.iterator();
        while (itr.hasNext()){
            if ((retVal = itr.next()).getSecret() == secret)
                break;
        }
        return retVal;
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

    public int isOccupied(int x0, int y0, int x1, int y1, int secret, boolean actual){
        int occupied = -1;
        for (EditGridLayout edit : this.editList){
            if (edit.getSecret() != secret) {
                float x2, y2, x3, y3;
                if (!actual) {
                    x2 = edit.getActualX();
                    y2 = edit.getActualY();
                    x3 = edit.getActualX() + edit.getNumCols() * edit.getCellLength();
                    y3 = edit.getActualY() + edit.getNumRows() * edit.getCellLength();
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
}