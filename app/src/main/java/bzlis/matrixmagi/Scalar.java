package bzlis.matrixmagi;

public class Scalar extends Matrix {


    public Scalar(){
        super(1, 1);
    }

    public Scalar(ComplexForm cf){
        super(new ComplexForm[][]{new ComplexForm[]{cf}});
    }

    @Override
    public Matrix mult(Matrix B){
        if (B instanceof Scalar)
            return new Scalar(ComplexForm.mult(this.getElement(0,0), B.getElement(0,0)));
        else {
            ComplexForm[][] retVal = new ComplexForm[B.getNumRows()][B.getNumCols()];
            for (int i = 0; i < B.getNumRows(); i++){
                for (int j = 0; j < B.getNumCols(); j++){
                    retVal[i][j] = ComplexForm.mult(this.getElement(0,0), B.getElement(i, j));
                }
            }
            return new Matrix(retVal);
        }
    }
}
