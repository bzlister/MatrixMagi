package com.example.bzlis.matrixmagi;

public class Scalar extends com.example.bzlis.matrixmagi.Matrix {

    public Scalar(){
        super(1, 1);
    }

    public Scalar(Double d){
        super(new Double[][]{new Double[]{d}});
    }

    @Override
    public Matrix mult(Matrix B){
        if (B instanceof Scalar)
            return new Scalar(this.getElement(0,0)* B.getElement(0,0));
        else {
            Double[][] retVal = new Double[B.getNumRows()][B.getNumCols()];
            for (int i = 0; i < B.getNumRows(); i++){
                for (int j = 0; j < B.getNumCols(); j++){
                    retVal[i][j] = this.getElement(0,0)*B.getElement(i, j);
                }
            }
            return new Matrix(retVal);
        }
    }

    public Matrix add(Matrix B){
        if (B instanceof Scalar)
            return new Scalar(this.getElement(0,0)+ B.getElement(0,0));
        else
            throw new IllegalArgumentException("Cannot add a scalar and a matrix!");
    }
}
