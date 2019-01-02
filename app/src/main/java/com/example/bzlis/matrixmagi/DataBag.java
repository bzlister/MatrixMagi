package com.example.bzlis.matrixmagi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class DataBag {

    private static DataBag instance = new DataBag();
    private HashSet<EditGridLayout> editList;
    private PixelGridView currView;

    private DataBag(){
        editList = new HashSet<>();
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
                //edit.removeAllViews();
                break;
            }
        }
    }

    public int getSize(){
        return editList.size();
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

    public HashSet<EditGridLayout> getData() {
        return editList;
    }

    public void setCurrView(PixelGridView px){
        this.currView = px;
    }

    public PixelGridView getCurrView(){
        return currView;
    }
}
