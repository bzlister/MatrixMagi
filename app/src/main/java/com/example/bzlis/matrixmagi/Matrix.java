package com.example.bzlis.matrixmagi;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

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

    public Matrix(Number... numbers){
        this.numRows = (int)Math.round(Math.sqrt(numbers.length));
        this.numCols = this.numRows;
        this.mat = new ComplexForm[numRows][numCols];
        for (int i = 0; i < numRows; i++){
            for (int j = 0; j < numCols; j++)
                mat[i][j] = new ComplexForm(numbers[numCols*i+j]);
        }
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
                tpose.setElement(this.getElement(i,j).conjugate(),j,i);
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
        Matrix H = this.duplicate();
        if (n < 0){
            n*=-1;
            H = this.inverse();
        }
        else if (n == 0)
            H = new Matrix(this.getNumRows(), this.getNumCols());
        else if (n > 1){
            Matrix half = H.power(n/2);
            if (n%2==0)
                H = half.mult(half);
            else
                H = H.mult(half.mult(half));
        }
        return H;
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

    protected ArrayList<Matrix> eigenVector() throws IllegalArgumentException {
        int fcount = 0;
        if (this.getNumCols() != this.getNumRows())
            throw new IllegalArgumentException("Not a square matrix!");
        int n = this.getNumRows();
        ArrayList<Scalar> eigenvalues = this.eigenValue();
        ArrayList<Matrix> eigenvectors = new ArrayList<>();
        for (Scalar eigen : eigenvalues) {
            Matrix A_new = this.duplicate().add(new Matrix(this.getNumRows(), this.getNumCols()).scalarMult(ComplexForm.mult(new ComplexForm(-1), eigen.getElement(0, 0))));
            for (int i = 0; i < n-1; i++){
                ComplexForm first = new ComplexForm(1);
                for (int j = 0; j < n; j++){
                    if (A_new.getElement(i, j).magnitude() > EPSILON) {
                        first = A_new.getElement(i, j).duplicate();
                        break;
                    }
                }
                for (int j2 = 0; j2 < n; j2++){
                    A_new.setElement(ComplexForm.div(A_new.getElement(i, j2), first), i, j2);
                }
                for (int i2 = i+1; i2 < n; i2++) {
                    ComplexForm second = new ComplexForm(1);
                    for (int y = 0; y < n; y++){
                        if (A_new.getElement(i2, y).magnitude() > EPSILON){
                            second = A_new.getElement(i2, y).duplicate();
                            break;
                        }
                    }
                    ComplexForm mult = ComplexForm.mult(new ComplexForm(-1), second);
                    for (int j3 = 0; j3 < n; j3++) {
                        A_new.setElement(ComplexForm.add(A_new.getElement(i2, j3), ComplexForm.mult(mult, A_new.getElement(i, j3))), i2, j3);
                    }
                }
            }
            for (int k = n-1; k > 0; k--){
                ComplexForm numerator = new ComplexForm(1);
                ComplexForm denominator = new ComplexForm(1);
                for (int h = 0; h < n; h++){
                    if (A_new.getElement(k-1, h).magnitude() > EPSILON && A_new.getElement(k, h).magnitude() > EPSILON) {
                        numerator = A_new.getElement(k-1, h);
                        denominator = A_new.getElement(k, h);
                        break;
                    }
                }
                ComplexForm mult = ComplexForm.div(numerator, denominator);
                for (int h2 = 0; h2 < n; h2++){
                    A_new.setElement(ComplexForm.sub(A_new.getElement(k-1,h2), ComplexForm.mult(mult, A_new.getElement(k, h2))), k-1, h2);
                }
            }
            for (int w = n-2; w >= 0; w--){
                boolean same = true;
                for (int t = 0; t < n; t++){
                    if (ComplexForm.add(A_new.getElement(w, t), A_new.getElement(w+1, t)).magnitude() > EPSILON){
                        same = false;
                        break;
                    }
                }
                if (same){
                    for (int e = 0; e < n; e++){
                        A_new.setElement(ComplexForm.add(A_new.getElement(w, e), A_new.getElement(w+1, e)), w+1, e);
                    }
                    ComplexForm first = new ComplexForm(1);
                    boolean searching = true;
                    for (int v = 0; v < n; v++){
                        if (A_new.getElement(w, v).magnitude() > EPSILON && searching){
                            first = A_new.getElement(w, v).duplicate();
                            searching = false;
                        }
                        A_new.setElement(ComplexForm.div(A_new.getElement(w, v), first), w, v);
                    }
                }
            }
            Matrix v = new Matrix(n, 1);
            for (int i = n-2; i >=0; i--){
                ComplexForm sum = new ComplexForm(0);
                if (A_new.getElement(i, i).equals(new ComplexForm(1))){
                    if (v.getElement(i+1, 0).equals(new ComplexForm(0)))
                        v.setElement(new ComplexForm(1), i + 1, 0);
                    for (int j = i + 1; j < n; j++)
                        sum = ComplexForm.add(sum, ComplexForm.mult(v.getElement(j, 0), ComplexForm.mult(new ComplexForm(-1), A_new.getElement(i, j))));
                    v.setElement(sum, i, 0);
                }
            }
            Matrix check = this.duplicate().add(new Matrix(this.getNumRows(), this.getNumCols()).scalarMult(ComplexForm.mult(new ComplexForm(-1), eigen.getElement(0, 0))));
           // if (check.mult(v).mag() < EPSILON)
                eigenvectors.add(v);
           // else
             //   fcount++;
        }
  //      if (fcount != this.numCols)
//            Toast.makeText(DataBag.getInstance().getCurrView().getContext(), "Found " + (this.numCols-fcount) + " out of " + this.numCols + " eigenvectors", Toast.LENGTH_SHORT).show();
        return eigenvectors;
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

    public ArrayList<Scalar> eigenValue() throws ArithmeticException{
        if (this.getNumCols() != this.getNumRows())
            throw new IllegalArgumentException("Not a square matrix!");
        int n = this.getNumCols();
        Matrix S = new Matrix(n, n);
        for (int a = 0; a < n; a++){
            for (int b = 0; b < n; b++){
                S.setElement(new ComplexForm(Math.random()*2-1), a, b);
            }
        }
        //S = new Matrix(-.9007, .88957, .80543, -.018272);
        //Matrix cp = S.mult(this.duplicate()).mult(S.inverse());
        Matrix cp = this.duplicate();
        for (int i = 0; i < 1000; i++){
            Matrix[] QR = cp.QR();
            cp = QR[1].mult(QR[0]);
        }
        ArrayList<Scalar> method1 = new ArrayList<Scalar>();
        ArrayList<Scalar> method2 = new ArrayList<Scalar>();
        int goochypoint = 0;
        if (n % 2 == 1) {
            Double min = 100000.0;
            int x = 0;
            while (x < n){
                ComplexForm goochyvalue = cp.getElement(x, x);
                Double det = this.add(new Matrix(n,n).scalarMult(ComplexForm.mult(goochyvalue, new ComplexForm(-1)))).det().magnitude();
                if (det < min){
                    min = det;
                    goochypoint = x;
                }
                x+=2;
            }
            method1.add(new Scalar(cp.getElement(goochypoint, goochypoint)));
        }
        int z = 0;
        int numEvaluates = 0;
        while (numEvaluates < n/2) {
            if (z == goochypoint && n%2 == 1)
                z++;
            ComplexForm Tr = ComplexForm.add(cp.getElement(z, z), cp.getElement(z + 1, z + 1));
            ComplexForm det = ComplexForm.sub(ComplexForm.mult(cp.getElement(z, z), cp.getElement(z + 1, z + 1)), ComplexForm.mult(cp.getElement(z, z + 1), cp.getElement(z + 1, z)));
            ComplexForm dscrm = ComplexForm.sqrt(ComplexForm.sub(ComplexForm.mult(Tr, Tr), ComplexForm.mult(new ComplexForm(4), det)));
            if (Double.isNaN(Tr.magnitude()) || Double.isNaN(det.magnitude()) || Double.isNaN(dscrm.magnitude()) || Tr.magnitude().isInfinite() || det.magnitude().isInfinite() || dscrm.magnitude().isInfinite())
                throw new ArithmeticException("Overflow error");
            method1.add(new Scalar(ComplexForm.div(ComplexForm.add(Tr, dscrm), new ComplexForm(2))));
            method1.add(new Scalar(ComplexForm.div(ComplexForm.sub(Tr, dscrm), new ComplexForm(2))));
            z+=2;
            numEvaluates++;
        }
        for (int y = 0; y < n; y++) {
            method2.add(new Scalar(cp.getElement(y, y)));
        }
        /*
        ArrayList<Scalar> retVal = new ArrayList();
        boolean method1Better = true;
        double max = 0;
        for (int s = 0; s < this.getNumCols(); s++){
            Double det1 = this.add(new Matrix(n,n).scalarMult(ComplexForm.mult(new ComplexForm(-1), method1.get(s).getElement(0,0)))).det().magnitude();
            Double det2 = this.add(new Matrix(n,n).scalarMult(ComplexForm.mult(new ComplexForm(-1), method2.get(s).getElement(0,0)))).det().magnitude();
            if (max < det1){
                method1Better = false;
                max = det1;
            }
            if (max < det2){
                method1Better = true;
                max = det2;
            }
        }
        if (method1Better)
            retVal = method1;
        else
            retVal = method2;
        */

        Iterator<Scalar> itr1 = method1.iterator();
        Iterator<Scalar> itr2 = method2.iterator();
        while (itr1.hasNext()){
            Double det1 = this.add(new Matrix(n,n).scalarMult(ComplexForm.mult(new ComplexForm(-1), itr1.next().getElement(0,0)))).det().magnitude();
            if (det1 > 1)
                itr1.remove();
        }
        while (itr2.hasNext()){
            Double det2 = this.add(new Matrix(n,n).scalarMult(ComplexForm.mult(new ComplexForm(-1), itr2.next().getElement(0,0)))).det().magnitude();
            if (det2 > 1)
                itr2.remove();
        }
        ArrayList<Scalar> retVal = (method1.size() >= method2.size()) ? method1 : method2;
       // if (retVal.size() != n)
           // Toast.makeText(DataBag.getInstance().getCurrView().getContext(), ("Found " + retVal.size() + " out of " + n + " eigenvalues."), Toast.LENGTH_SHORT).show();
        return retVal;
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
            ComplexForm mag = ComplexForm.sqrt((u.transpose().mult(u)).getElement(0,0));
            E.add(u.scalarMult(ComplexForm.div(new ComplexForm(1), mag).correct()));
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
        ComplexForm sum = new ComplexForm(0);
        for (int i = 0; i < v.getNumRows(); i++){
            sum = ComplexForm.add(sum, ComplexForm.mult(v.getElement(i,0).conjugate(), w.getElement(i,0)));
        }
        return sum;
    }


    @Override
    public String toString(){
        String retVal = "\n";
        for (int i = 0; i < numRows; i++) {
            retVal += "\n";
            for (int j = 0; j < numCols; j++) {
                retVal += this.getElement(i,j).getPrettyString() + ", ";
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