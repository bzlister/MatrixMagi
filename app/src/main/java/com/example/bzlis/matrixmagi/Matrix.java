package com.example.bzlis.matrixmagi;

public class Matrix {

    private Double[][] mat;
    private int numRows, numCols;

    public Matrix(int m, int n){
        this.numRows = m;
        this.numCols = n;
        mat = new Double[numRows][numCols];
    }

    public Matrix(Double[][] mat){
        this.mat = mat;
        this.numRows = mat.length;
        this.numCols = mat[0].length;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols(){
        return this.numCols;
    }

    public void setElement(Double d, int i, int j){
        mat[i][j] = d;
    }

    public Double getElement(int i, int j){
        if (mat[i][j] == null)
            return 0.0;
        else
            return mat[i][j];
    }

    public Matrix add(Matrix B) throws IllegalArgumentException {
        if (B instanceof Scalar)
            return B.add(this);
        if ((this.numCols != B.numCols) || (this.numRows != B.numRows))
            throw new IllegalArgumentException("To add, matrices must have the same number of rows and columns!");
        Double[][] retVal = new Double[this.numRows][this.numCols];
        for (int i = 0; i < this.numRows; i++){
            for (int j = 0; j < this.numCols; j++){
                retVal[i][j] = this.getElement(i, j)+B.getElement(i, j);
            }
        }
        return new Matrix(retVal);
    }

    public Matrix mult(Matrix B) throws IllegalArgumentException{
        if (B instanceof Scalar){
            return B.mult(this);
        }
        if (this.numCols != B.numRows)
            throw new IllegalArgumentException("To multiply, the number of columns in the first matrix must equal the number of rows in the second!");
        Double[][] retVal = new Double[this.numRows][B.numCols];
        for (int i = 0; i < this.numRows; i++){
            for (int j = 0; j < B.numCols; j++){
                double sum = 0.0;
                for (int k = 0; k < this.numCols; k++)
                    sum += this.getElement(i,k)* B.getElement(k,j);
                retVal[i][j] = sum;
            }
        }
        return new Matrix(retVal);
    }

    @Override
    public String toString(){
        String retVal = "\n";
        for (int i = 0; i < numRows; i++) {
            retVal += "\n";
            for (int j = 0; j < numCols; j++) {
                retVal += mat[i][j] + " ";
            }
        }
        return retVal;
    }

}
