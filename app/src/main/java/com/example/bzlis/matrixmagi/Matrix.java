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
            return (numRows == numCols && i == j) ? 1 : 0.0;
        else
            return mat[i][j];
    }

    public Matrix add(Matrix B) throws IllegalArgumentException {
        if (B instanceof Scalar)
            return B.add(this);
        if ((this.numCols != B.numCols) || (this.numRows != B.numRows)) {
            String s = "";
            if (this.numRows != B.numRows)
                s = "Rows(A) =/= Rows(B)\n";
            if (this.numCols != B.numCols)
                s +="Cols(A) =/= Cols(B)\n";
            throw new IllegalArgumentException(s.substring(0,s.length()-1));
        }
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
            throw new IllegalArgumentException("Cols(A) =/= Rows(B)");
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

    public Matrix guassElim(Matrix B) throws IllegalArgumentException, ArithmeticException{
        double EPSILON = 1e-10;
            int n = B.getNumRows();
            //|| (this.getNumCols() != this.getNumRows())
            if ((B.getNumCols() > 1) || (B.getNumRows() != this.getNumRows())) {
                String s = "";
                if (this.getNumCols() != this.getNumRows())
                    s = "Rows(A) =/= Cols(A)\n";
                if (B.getNumCols() > 1)
                    s += "Cols(B) > 1\n";
                if (B.getNumRows() != this.getNumRows())
                    s += "Rows(A) != Rows(B)\n";
                throw new IllegalArgumentException(s.substring(0,s.length()-1));
            }
            for (int p = 0; p < Math.min(n, this.getNumCols()); p++) {
                int max = p;
                for (int i = p + 1; i < n; i++) {
                    if (Math.abs(this.getElement(i,p)) > Math.abs(this.getElement(max,p)))
                        max = i;
                }
                this.swapRows(p, max);
                B.swapRows(p, max);
                if (Math.abs(this.getElement(p,p)) <= EPSILON)
                    throw new ArithmeticException("A is singular or nearly singular");
                for (int i = p + 1; i < n; i++) {
                    double alpha = this.getElement(i,p)/this.getElement(p,p);
                    B.setElement(B.getElement(i,0)-alpha*B.getElement(p,0),i,0);
                    for (int j = p; j < Math.min(n, this.getNumCols()); j++)
                        this.setElement(this.getElement(i,j)-alpha*this.getElement(p,j),i,j);
                }
            }
            Double[][] x = new Double[Math.min(n, this.getNumCols())][1];
            for (int i = Math.min(this.getNumCols()-1, n - 1); i >= 0; i--) {
                double sum = 0.0;
                for (int j = i + 1; j < Math.min(n, this.getNumCols()); j++)
                    sum += (x[j][0] != null) ? this.getElement(i,j)*x[j][0] : 0;
                x[i][0] = (B.getElement(i,0) - sum) / this.getElement(i,i);
            }
            return new Matrix(x);
        }


    private void swapRows(int r1, int r2) {
        Double[] temp = this.mat[r1];
        this.mat[r1] = this.mat[r2];
        this.mat[r2] = temp;
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
