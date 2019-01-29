package com.example.bzlis.matrixmagi;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Matrix {

    private ComplexForm[][] mat;
    private int numRows, numCols;
    private double error = 0.0;
    private final double EPSILON = 1e-10;

    public Matrix(int m, int n){
        this.numRows = m;
        this.numCols = n;
        mat = new ComplexForm[numRows][numCols];
    }

    public Matrix(ComplexForm[][] mat){
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

    public void setElement(ComplexForm cf, int i, int j){
        mat[i][j] = cf;
    }

    public ComplexForm getElement(int i, int j){
        if (mat[i][j] == null)
            return ((numRows == numCols) && (i == j)) ? new ComplexForm(1.0) : new ComplexForm(0.0);
        else
            return mat[i][j];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        if (numRows != matrix.numRows || numCols != matrix.numCols)
            return false;
        boolean match = true;
        for (int i = 0; i < numRows; i++){
            for (int j = 0; j < numCols; j++){
                if (!this.getElement(i, j).equals(matrix.getElement(i, j))) {
                    match = false;
                    i = numRows;
                    j = numCols;
                }
            }
        }
        return match;
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
        ComplexForm[][] retVal = new ComplexForm[this.numRows][this.numCols];
        for (int i = 0; i < this.numRows; i++){
            for (int j = 0; j < this.numCols; j++){
                retVal[i][j] = ComplexForm.add(this.getElement(i, j), B.getElement(i, j));
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
        ComplexForm[][] retVal = new ComplexForm[this.numRows][B.numCols];
        for (int i = 0; i < this.numRows; i++){
            for (int j = 0; j < B.numCols; j++){
                ComplexForm sum = new ComplexForm(0.0);
                for (int k = 0; k < this.numCols; k++)
                    sum = ComplexForm.add(sum, ComplexForm.mult(this.getElement(i,k), B.getElement(k,j)));
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
        Matrix T = this.transpose();
        String message = "";
        if (this.getNumRows() != B.getNumRows())
            throw new IllegalArgumentException("Rows(A) =/= Rows(B)");
        if (this.getNumRows() >= this.getNumCols()) {
            X = T.mult(this).inverse().mult(T).mult(B);
            if (this.getNumRows() != this.getNumCols())
                message = "System is overdetermined!";
        }
        else {
            X = T.mult(this.mult(T).inverse()).mult(B);
            message = "System is underdetermined!";
        }
        X.error = this.mult(X).sumSquaredErrors(B);
        if (!message.equals(""))
            Toast.makeText(DataBag.getInstance().getCurrView().getContext(), message, Toast.LENGTH_LONG).show();
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
            throw new IllegalArgumentException("Not a square matrix!");
        int n = this.getNumRows();
        Matrix inv = new Matrix(n, n);
        for (int i = 0; i < n; i++){
            ComplexForm alpha = cp.getElement(i,i);
            if (alpha.magnitude() <= EPSILON)
                throw new IllegalArgumentException("Singular or nearly singular!");
            for (int j = 0; j < n; j++){
                cp.setElement(ComplexForm.div(cp.getElement(i,j), alpha),i,j);
                inv.setElement(ComplexForm.div(inv.getElement(i,j), alpha),i,j);
            }
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    ComplexForm beta = cp.getElement(k, i);
                    for (int z = 0; z < n; z++) {
                        cp.setElement(ComplexForm.sub(cp.getElement(k, z), ComplexForm.mult(beta, cp.getElement(i, z))), k, z);
                        inv.setElement(ComplexForm.sub(inv.getElement(k, z), ComplexForm.mult(beta, inv.getElement(i, z))), k, z);
                    }
                }
            }
        }
        inv.error = this.mult(inv).sumSquaredErrors(new Matrix(n,n));
        return inv;
    }

    protected Double sumSquaredErrors(Matrix B){
        ComplexForm sum = new ComplexForm(0);
        if ((this.getNumCols() != B.getNumCols()) || (this.getNumRows() != B.getNumRows())){
            String s = "";
            if (this.numRows != B.numRows)
                s = "Rows(A) =/= Rows(B)\n";
            if (this.numCols != B.numCols)
                s +="Cols(A) =/= Cols(B)\n";
            throw new IllegalArgumentException(s.substring(0,s.length()-1));
        }
        for (int i = 0; i < this.getNumRows(); i++){
            for (int j = 0; j < this.getNumCols(); j++) {
                ComplexForm factor = ComplexForm.sub(this.getElement(i, j), B.getElement(i, j));
                sum = ComplexForm.add(sum, ComplexForm.mult(factor, factor));
            }
        }
        return sum.magnitude();
    }

    protected ComplexForm det() throws IllegalArgumentException {
        if (this.getNumRows() != this.getNumCols())
            throw new IllegalArgumentException("Not a square matrix!");
        ComplexForm det = new ComplexForm(0);
        if (this.getNumRows() == 2)
            det = ComplexForm.sub(ComplexForm.mult(this.getElement(0,0), this.getElement(1,1)), ComplexForm.mult(this.getElement(0,1), this.getElement(1,0)));
        else {
            ComplexForm mult = new ComplexForm(1);
            for (int k = 0; k < this.getNumCols(); k++) {
                Matrix cofactor = new Matrix(this.getNumRows()-1, this.getNumCols()-1);
                for (int i = 1; i < this.getNumRows(); i++){
                    int j2 = 0;
                    for (int j = 0; j < this.getNumCols(); j++){
                        if (j != k) {
                            cofactor.setElement(this.getElement(i, j), i - 1, j2);
                            j2++;
                        }
                    }
                }
                det = ComplexForm.add(det, ComplexForm.mult(ComplexForm.mult(mult, this.getElement(0, k)), cofactor.det()));
                mult = ComplexForm.mult(mult, new ComplexForm(-1));
            }
        }
        return det;
    }

    protected Matrix guassElim(Matrix B) throws IllegalArgumentException {
        int n = B.getNumRows();
        if (B.getNumRows() != this.getNumRows())
                throw new IllegalArgumentException();
        if (numRows == numCols) {
            Matrix A_new = this.duplicate();
            Matrix B_new = B.duplicate();
            for (int p = 0; p < n; p++) {
                int max = p;
                for (int i = p + 1; i < n; i++) {
                    if (A_new.getElement(i, p).magnitude() > A_new.getElement(max, p).magnitude())
                        max = i;
                }
                A_new.swapRows(p, max);
                B_new.swapRows(p, max);
                if (A_new.getElement(p, p).magnitude() < EPSILON)
                    throw new IllegalArgumentException("A is singular or nearly singular");
                for (int i = p + 1; i < n; i++) {
                    ComplexForm alpha = ComplexForm.div(A_new.getElement(i, p), A_new.getElement(p, p));
                    for (int j = p; j < n; j++) {
                        A_new.setElement(ComplexForm.sub(A_new.getElement(i, j), ComplexForm.mult(alpha, A_new.getElement(p, j))), i, j);
                        B_new.setElement(ComplexForm.sub(B_new.getElement(i, j), ComplexForm.mult(alpha, B_new.getElement(p, j))), i, j);
                    }
                }
            }
            ComplexForm[][] x = new ComplexForm[this.getNumCols()][B.getNumCols()];
            for (int z = B.getNumCols() - 1; z >= 0; z--) {
                for (int i = this.getNumCols() - 1; i >= 0; i--) {
                    ComplexForm sum = new ComplexForm(0);
                    for (int j = i + 1; j < this.getNumCols(); j++)
                        sum = ComplexForm.add(sum, ComplexForm.mult(A_new.getElement(i, j), x[j][z]));
                    x[i][z] = ComplexForm.div(ComplexForm.sub(B_new.getElement(i, z), sum), A_new.getElement(i, i));
                }
            }
            Matrix X = new Matrix(x);
            // X.error = (this.mult(X)).sumSquaredErrors(B);
            return X;
        }
        else
            return this.leastSquares(B);
     }


    private void swapRows(int r1, int r2) {
        ComplexForm[] temp = this.mat[r1];
        this.mat[r1] = this.mat[r2];
        this.mat[r2] = temp;
    }

    protected Matrix scalarMult(ComplexForm cf){
        Matrix prod = this.duplicate();
        for (int i =  0; i < this.getNumRows(); i++){
            for (int j = 0; j < this.getNumCols(); j++)
                prod.setElement(ComplexForm.mult(prod.getElement(i,j), cf), i, j);
        }
        return prod;
    }

    public Matrix getCol(int j){
        ComplexForm[][] col = new ComplexForm[this.getNumRows()][1];
        for (int i = 0; i < this.getNumRows(); i++)
            col[i][0] = this.getElement(i, j);
        return new Matrix(col);
    }

    public double mag(){
        if (this.getNumRows() != 1 && this.getNumCols() != 1)
            throw new IllegalArgumentException("Matrix must be 1xn or nx1");
        double sum = 0.0;
        if (this.getNumRows() == 1){
            for (int j = 0; j < this.getNumCols(); j++)
                sum += Math.pow(this.getElement(0,j).magnitude(), 2);
        }
        else if (this.getNumCols() == 1){
            for (int i = 0; i < this.getNumRows(); i++)
                sum += Math.pow(this.getElement(i,0).magnitude(), 2);
        }
        return Math.sqrt(sum);
    }

    public ArrayList<Scalar> eigen(){
        if (this.getNumCols() != this.getNumRows())
            throw new IllegalArgumentException("Not a square matrix!");
        Matrix cp = this.duplicate();
        ArrayList<Scalar> lambda = new ArrayList<>();
        for (int i = 0; i < 500; i++){
            Matrix[] QR = cp.QR();
            cp = QR[1].mult(QR[0]);
        }
        boolean hess = false;
        for (int t = 0; t < cp.getNumCols()-1; t++){
            if (cp.getElement(t+1, t).magnitude() > EPSILON)
                hess = true;
        }
        if (hess){
            for (int z = 0; z < cp.getNumCols()-1; z++){
                ComplexForm Tr = ComplexForm.add(cp.getElement(z, z), cp.getElement(z+1, z+1));
                ComplexForm det = ComplexForm.sub(ComplexForm.mult(cp.getElement(z, z), cp.getElement(z+1, z+1)), ComplexForm.mult(cp.getElement(z, z+1), cp.getElement(z+1, z)));
                ComplexForm dscrm = ComplexForm.sqrt(ComplexForm.sub(ComplexForm.div(ComplexForm.mult(Tr, Tr), new ComplexForm(4)), det));
                if (dscrm.isComplex()){
                    lambda.add(new Scalar(ComplexForm.add(ComplexForm.div(Tr, new ComplexForm(2)), dscrm)));
                    lambda.add(new Scalar(ComplexForm.sub(ComplexForm.div(Tr, new ComplexForm(2)), dscrm)));
                }
                else{
                    ComplexForm L1 = ComplexForm.add(ComplexForm.div(Tr, new ComplexForm(2)), dscrm);
                    ComplexForm L2 = ComplexForm.sub(ComplexForm.div(Tr, new ComplexForm(2)), dscrm);
                    Matrix A1 = this.duplicate().add(new Matrix(this.getNumRows(), this.getNumCols()).scalarMult(ComplexForm.mult(L1, new ComplexForm(-1))));
                    Matrix A2 = this.duplicate().add(new Matrix(this.getNumRows(), this.getNumCols()).scalarMult(ComplexForm.mult(L2, new ComplexForm(-1))));
                    if (A1.det().magnitude() < A2.det().magnitude())
                        lambda.add(new Scalar(L1));
                    else
                        lambda.add(new Scalar(L2));
                }
            }
        }
        else {
            for (int y = 0; y < cp.getNumCols(); y++) {
                lambda.add(new Scalar(cp.getElement(y, y)));
            }
        }
        return lambda;
    }

    public Matrix[] QR(){
        ArrayList<Matrix> U = new ArrayList();
        ArrayList<Matrix> E = new ArrayList();
        ArrayList<Matrix> A = new ArrayList();
        for (int j = 0; j < this.getNumCols(); j++) {
            Matrix a = this.getCol(j);
            Matrix u = a.duplicate();
            for (int k = 0; k < j; k++) {
                u = u.add((proj(U.get(k), a).scalarMult(new ComplexForm(-1))));
            }
            U.add(u);
            Double scal = 1 / u.mag();
            if (scal == Double.POSITIVE_INFINITY)
                scal = Double.MAX_VALUE;
            E.add(u.scalarMult(new ComplexForm(scal)));
            int z = 0;
            do {
                a = a.add(E.get(z).scalarMult(innerProd(E.get(z), a)));
                z++;
            } while (z < j);
            A.add(a);
        }
        Matrix Q = new Matrix(this.getNumRows(), this.getNumCols());
        for (int j = 0; j < this.getNumCols(); j++){
            for (int i = 0; i < this.getNumRows(); i++)
                Q.setElement(E.get(j).getElement(i, 0), i, j);
        }
        return new Matrix[]{Q, Q.transpose().mult(this)};
    }

    public Matrix proj(Matrix u, Matrix a){
        return u.scalarMult(ComplexForm.div(innerProd(u,a), innerProd(u,u)));
    }

    public ComplexForm innerProd(Matrix v, Matrix w){
        return v.transpose().mult(w).getElement(0,0);
    }


    @Override
    public String toString(){
        String retVal = "\n";
        for (int i = 0; i < numRows; i++) {
            retVal += "\n";
            for (int j = 0; j < numCols; j++) {
                retVal += this.getElement(i,j).getPrettyString() + " ";
            }
        }
        return retVal;
    }

    protected Matrix duplicate(){
        ComplexForm[][] clone = new ComplexForm[this.getNumRows()][this.getNumCols()];
        for (int i = 0; i < this.getNumRows(); i++){
            for (int j = 0; j < this.getNumCols(); j++){
                ComplexForm cf = (this.getElement(i,j) == null) ? new ComplexForm(0) : this.getElement(i,j);
                clone[i][j] = cf;
            }
        }
        return new Matrix(clone);
    }

    protected double getError(){
        return this.error;
    }

}