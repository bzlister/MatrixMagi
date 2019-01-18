package com.example.bzlis.matrixmagi;

public class Scalar extends com.example.bzlis.matrixmagi.Matrix {

    private Double complex;

    public Scalar(){
        super(1, 1);
    }

    public Scalar(Double d){
        super(new Double[][]{new Double[]{d}});
    }

    public void setComplex(Double d){
        this.complex = d;
    }

    public Double getComplex(){
        return (this.complex == null) ? 0.0 : this.complex;
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
}
