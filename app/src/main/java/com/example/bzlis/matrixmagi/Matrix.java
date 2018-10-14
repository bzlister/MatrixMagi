package com.example.bzlis.matrixmagi;

import java.util.Locale;

public class Matrix {

    private Double[][] mat;
    private int numRows, numCols;
    private double error = 0.0;

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

    public static String getPrettyString(Double d){
        String s = "";
        if (d == null)
            d = 0.0;
        if (d == (long)(1.0*d))
            s = String.format(Locale.getDefault(), "%d", (int)(1.0*d));
        else
            s = String.format(Locale.getDefault(), "%g", d);
        return s;
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


    public Matrix transpose() {
        Matrix tpose = new Matrix(this.getNumCols(), this.getNumRows());
        for (int i = 0; i < this.getNumRows(); i++){
            for (int j = 0; j < this.getNumCols(); j++)
                tpose.setElement(this.getElement(i,j),j,i);
        }
        return tpose;
    }

    protected Matrix leastSquares(Matrix B) throws IllegalArgumentException {
        Matrix X = null;
        try{
            X = this.guassElim(B);
        } catch (IllegalArgumentException e){
            try {
                if (this.getNumRows() > this.getNumCols()) {
                    Matrix T = this.transpose();
                    X = T.mult(this).inverse().mult(T).mult(B);
                } else if (this.getNumRows() < this.getNumCols()) {
                    Matrix T = this.transpose();
                    X = T.mult(this.mult(T).inverse()).mult(B);
                } else
                    throw new IllegalArgumentException("");
            } catch (IllegalArgumentException f){
                throw new IllegalArgumentException("No solution");
            }
            X.error = this.mult(X).sumSquaredErrors(B);
        }
        return X;
    }

    protected Matrix power(int n){
        if (this.getNumRows() != this.getNumCols())
            throw new IllegalArgumentException("Rows(A) =/= Cols(A)");
        Matrix H;
        if (n < 0){
            n*=-1;
            H = this.inverse();
        }
        else if (n == 0)
            H = new Matrix(this.getNumRows(), this.getNumCols());
        else
            H = this.duplicate();
        if (n == 1)
            return H;
        else{
            Matrix half = H.power(n/2);
            if (n%2==0)
                return half.mult(half);
            else
                return H.mult(half.mult(half));
        }
    }


    protected Matrix inverse() throws IllegalArgumentException{
        Matrix cp = this.duplicate();
        double EPSILON = 1e-10;
        if (this.getNumCols() != this.getNumRows())
            throw new IllegalArgumentException("Rows(A) =/= Cols(A)");
        int n = this.getNumRows();
        Matrix inv = new Matrix(n, n);
        for (int i = 0; i < n; i++){
            double alpha = cp.getElement(i,i);
            if (Math.abs(alpha) <= EPSILON)
                throw new IllegalArgumentException("A is singular or nearly singular");
            for (int j = 0; j < n; j++){
                cp.setElement(cp.getElement(i,j)/alpha,i,j);
                inv.setElement(inv.getElement(i,j)/alpha,i,j);
            }
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double beta = cp.getElement(k, i);
                    for (int z = 0; z < n; z++) {
                        cp.setElement(cp.getElement(k, z) - beta * cp.getElement(i, z), k, z);
                        inv.setElement(inv.getElement(k, z) - beta * inv.getElement(i, z), k, z);
                    }
                }
            }
        }
        inv.error = this.mult(inv).sumSquaredErrors(new Matrix(n,n));
        return inv;
    }

    protected Double sumSquaredErrors(Matrix B){
        double sum = 0;
        if ((this.getNumCols() != B.getNumCols()) || (this.getNumRows() != B.getNumRows())){
            String s = "";
            if (this.numRows != B.numRows)
                s = "Rows(A) =/= Rows(B)\n";
            if (this.numCols != B.numCols)
                s +="Cols(A) =/= Cols(B)\n";
            throw new IllegalArgumentException(s.substring(0,s.length()-1));
        }
        for (int i = 0; i < this.getNumRows(); i++){
            for (int j = 0; j < this.getNumCols(); j++)
                sum += Math.pow(this.getElement(i,j)-B.getElement(i,j),2);
        }
        return sum;
    }

    protected Matrix guassElim(Matrix B) throws IllegalArgumentException {
        double EPSILON = 1e-10;
        int n = B.getNumRows();
        if ((this.getNumCols() != this.getNumRows()) || (B.getNumRows() != this.getNumRows()) || (B.getNumCols() > 1))
                throw new IllegalArgumentException();
        Matrix A_new = this.duplicate();
        Matrix B_new = B.duplicate();
        for (int p = 0; p < n; p++) {
            int max = p;
            for (int i = p + 1; i < n; i++) {
                if (Math.abs(A_new.getElement(i,p)) > Math.abs(A_new.getElement(max,p)))
                    max = i;
            }
            A_new.swapRows(p, max);
            B_new.swapRows(p, max);
            if (Math.abs(A_new.getElement(p,p)) <= EPSILON)
                throw new IllegalArgumentException("A is singular or nearly singular");
            for (int i = p + 1; i < n; i++) {
                double alpha = A_new.getElement(i,p)/A_new.getElement(p,p);
                B_new.setElement(B_new.getElement(i,0)-alpha*B_new.getElement(p,0),i,0);
                    for (int j = p; j < n; j++) {
                        A_new.setElement(A_new.getElement(i, j) - alpha * A_new.getElement(p, j), i, j);
                    }
                }
            }
            Double[][] x = new Double[n][1];
            for (int i = n - 1; i >= 0; i--) {
                double sum = 0.0;
                for (int j = i + 1; j < n; j++)
                    sum += (x[j][0] != null) ? A_new.getElement(i, j) * x[j][0] : 0;
                x[i][0] = (B_new.getElement(i, 0) - sum) / A_new.getElement(i, i);
            }
            Matrix X = new Matrix(x);
            X.error = (this.mult(X)).sumSquaredErrors(B);
            return X;
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
                retVal += Matrix.getPrettyString(this.getElement(i,j)) + " ";
            }
        }
        return retVal;
    }

    protected Matrix duplicate(){
        Double[][] clone = new Double[this.getNumRows()][this.getNumCols()];
        for (int i = 0; i < this.getNumRows(); i++){
            for (int j = 0; j < this.getNumCols(); j++){
                double d = (this.getElement(i,j) == null) ? 0 : this.getElement(i,j);
                clone[i][j] = d;
            }
        }
        return new Matrix(clone);
    }

    protected Double getError(){
        return this.error;
    }
}
/*
                String s = "";
                if (this.getNumCols() != this.getNumRows())
                    s = "Rows(A) =/= Cols(A)\n";
                if (B.getNumCols() > 1)
                    s += "Cols(B) > 1\n";
                if (B.getNumRows() != this.getNumRows())
                    s += "Rows(A) != Rows(B)\n";
 */

/*
     double EPSILON = 1e-10;
        for (int z = 0; z < n; z++){
            if (Math.abs(this.getElement(z,z)) <= EPSILON)
                throw new IllegalArgumentException("A is singular or nearly singular");
            for (int i  = z+1 ; i < n; i++){
                double alpha = this.getElement(i,z)/this.getElement(z,z);
                I.setElement(I.getElement(i,z)-alpha*I.getElement(z,z),i,z);
                for (int j = z; j < n; j++)
                    this.setElement(this.getElement(i,j) - alpha*this.getElement(z,j),i,j);
            }
        }
        Double[][] inv = new Double[n][n];
        for (int q = 0; q < n; q++) {
            for (int i = n - 1; i >= 0; i--) {
                double sum = 0.0;
                for (int j = i + 1; j < n; j++)
                    sum += (inv[j][q] != null) ? this.getElement(i, j) * inv[j][q] : 0;
                inv[i][q] = (I.getElement(i, q) - sum) / this.getElement(i, i);
            }
        }
 */