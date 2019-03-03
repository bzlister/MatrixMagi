package bzlis.matrixmagi;

import org.junit.Test;

import java.util.ArrayList;

import bzlis.matrixmagi.ComplexForm;
import bzlis.matrixmagi.Matrix;
import bzlis.matrixmagi.Scalar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

/*
    @Test
    public void testEigen(){
        Matrix A = new Matrix(3, 3);
        A.setElement(12.0, 0, 0);
        A.setElement(-51.0, 0, 1);
        A.setElement(4.0, 0, 2);
        A.setElement(6.0, 1, 0);
        A.setElement(167.0, 1, 1);
        A.setElement(-68.0, 1, 2);
        A.setElement(-4.0, 2, 0);
        A.setElement(24.0, 2, 1);
        A.setElement(-41.0, 2, 2);
        System.out.println(A.eigen());
        assert(true);
    }
*/

    @Test
    public void testAdd(){
        Matrix A = new Matrix(2, 1);
        Matrix B = new Matrix(2, 1);
        A.setElement(new ComplexForm(1, 2), 0, 0);
        A.setElement(new ComplexForm(2, -1), 1, 0);
        B.setElement(new ComplexForm(5, 10), 0, 0);
        B.setElement(new ComplexForm(5, 10), 1, 0);
        Matrix C = A.add(B);
        assertEquals(new ComplexForm(6, 12), C.getElement(0, 0));
        assertEquals(new ComplexForm(7, 9), C.getElement(1, 0));
    }

    @Test
    public void testMult(){
        Matrix A = new Matrix(2, 1);
        Matrix B = new Matrix(1, 2);
        A.setElement(new ComplexForm(5, -6.5),0, 0);
        A.setElement(new ComplexForm(8, 1), 1, 0);
        B.setElement(new ComplexForm(0, 4), 0, 0);
        B.setElement(new ComplexForm(4, 0), 0, 1);
        Matrix C = B.mult(A);
        assertEquals(1, C.getNumCols());
        assertEquals(1, C.getNumRows());
        assertEquals(new ComplexForm(58, 24), C.getElement(0, 0));
    }

    @Test
    public void testComplex(){
        ComplexForm a = new ComplexForm(3, 4);
        ComplexForm b = new ComplexForm(8, -2);
        assert(ComplexForm.add(a, b).equals(new ComplexForm(11, 2)));
        assert(ComplexForm.mult(a, b).equals(new ComplexForm(32, 26)));
        assert(ComplexForm.div(a, b).equals(new ComplexForm(4.0/17, 19.0/34)));
        ComplexForm c = new ComplexForm(1, 1);
        ComplexForm d = new ComplexForm(1, -1);
        assert(ComplexForm.mult(c, d).equals(new ComplexForm(2, 0)));
    }

    @Test
    public void testPower(){
        Matrix A = new Matrix(2, 2);
        A.setElement(new ComplexForm(0, 1), 0, 0);
        A.setElement(new ComplexForm(0, 1), 0, 1);
        A.setElement(new ComplexForm(1, 0), 1, 0);
        A.setElement(new ComplexForm(1, 0), 1, 1);
        Matrix A2 = A.power(2);
        assertEquals(new ComplexForm(-1, 1), A2.getElement(0, 0));
        assertEquals(new ComplexForm(-1, 1), A2.getElement(0, 1));
        assertEquals(new ComplexForm(1, 1), A2.getElement(1, 0));
        assertEquals(new ComplexForm(1, 1), A2.getElement(1, 1));
    }

    @Test
    public void testEquals(){
        Matrix A = new Matrix(2, 1);
        A.setElement(new ComplexForm(4.5, 2.3), 0, 0);
        A.setElement(new ComplexForm(3.4, -1.2), 1, 0);
        Matrix B = A.add(new Matrix(2, 1));
        assertEquals(A, B);
    }

    @Test
    public void testInverse(){
        Matrix A = new Matrix(2, 2);
        A.setElement(new ComplexForm(1), 0, 0);
        A.setElement(new ComplexForm(0, -1), 0, 1);
        A.setElement(new ComplexForm(1), 1, 0);
        A.setElement(new ComplexForm(0, 1), 1, 1);
        Matrix B = A.inverse();
        System.out.println(B.toString());
        assertEquals(new ComplexForm(0.5), B.getElement(0, 0));
        assertEquals(new ComplexForm(0.5), B.getElement(0, 1));
        assertEquals(new ComplexForm(0, 0.5), B.getElement(1, 0));
        assertEquals(new ComplexForm(0, -0.5), B.getElement(1, 1));
        assertEquals(A.mult(B), new Matrix(2, 2));
    }

    @Test
    public void testLSRandom(){
        for (int i = 0; i < 1000; i++){
            int m = (int)(Math.random()*10)+1;
            int n = (int)(Math.random()*10)+1;
            int o = (int)(Math.random()*10)+1;
            Matrix A = new Matrix(m, n);
            Matrix X = new Matrix(n, o);
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < m; k++)
                    A.setElement(new ComplexForm(Math.random() * 100 - 50, Math.random()*100-50), k, j);
                for (int p = 0; p < o; p++)
                    X.setElement(new ComplexForm(Math.random() * 100 - 50, Math.random()*100-50), j, p);
            }
            try {
                Matrix X2 = A.leastSquares(A.mult(X));
                assert(X2.getError() < 1e-10);
            } catch (IllegalArgumentException e) {
                fail("Beeeg Yoshi");
            }
        }
    }

    @Test
    public void testComplexFormParse(){
        try {
            assertEquals(new ComplexForm(3, 2), ComplexForm.parse("3+2i"));
            assertEquals(new ComplexForm(3, 2), ComplexForm.parse("3+i2"));
            assertEquals(new ComplexForm(3, 2), ComplexForm.parse("3.0+2.0i"));
            assertEquals(new ComplexForm(3, 2), ComplexForm.parse("3.0+i2.0"));
            assertEquals(new ComplexForm(-3, 2), ComplexForm.parse("-3+2i"));
            assertEquals(new ComplexForm(3, -2), ComplexForm.parse("3-2i"));
            assertEquals(new ComplexForm(0, 2), ComplexForm.parse("2i"));
            assertEquals(new ComplexForm(0, -2.1), ComplexForm.parse("-2.1i"));
            assertEquals(new ComplexForm(-3), ComplexForm.parse("-3"));
            assertEquals(new ComplexForm(0), ComplexForm.parse("0"));
            assertEquals(new ComplexForm(0), ComplexForm.parse("0+0i"));
            assertEquals(new ComplexForm(0), ComplexForm.parse("0-0i"));
        } catch (Exception p){
            System.out.println(p.getMessage());
            fail();
        }
    }

    @Test
    public void testMag(){
        for (int numT = 0; numT < 100; numT++) {
            int n = (int) (Math.random() * 9) + 2;
            Matrix z = new Matrix(n, 1);
            for (int i = 0; i < n; i++)
                z.setElement(new ComplexForm(Math.random() * 100 - 50, Math.random() * 100 - 50), i, 0);
            assertEquals(ComplexForm.sqrt((z.transpose().mult(z)).getElement(0, 0)).getReal(), new Double(z.mag()), 1e-5);
        }
    }

    @Test
    public void testQR(){
        Matrix A = new Matrix(2, 2);
        A.setElement(new ComplexForm(0,3),0,0);
        A.setElement(new ComplexForm(2,-2),0,1);
        A.setElement(new ComplexForm(4,7),1,0);
        A.setElement(new ComplexForm(-1,5),1,1);
        Matrix[] QR = A.QR();
        System.out.print(QR[0]);
        System.out.print(QR[1]);
        System.out.print(QR[0].mult(QR[1]));
    }


    @Test
    public void testFunkyC(){
        Matrix A = new Matrix(3, 2);
        assertEquals(3, A.getNumRows());
        assertEquals(2, A.getNumCols());
        Matrix B = new Matrix(1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(3, B.getNumRows());
        assertEquals(3, B.getNumCols());
    }


    @Test
    public void testEigen(){
        Matrix A = new Matrix(3, 3);
        A.setElement(new ComplexForm(1), 0, 0);
        A.setElement(new ComplexForm(2), 0, 1);
        A.setElement(new ComplexForm(3), 0, 2);
        A.setElement(new ComplexForm(0), 1, 0);
        A.setElement(new ComplexForm(1),1, 1);
        A.setElement(new ComplexForm(2), 1, 2);
        A.setElement(new ComplexForm(-1), 2, 0);
        A.setElement(new ComplexForm(0), 2, 1);
        A.setElement(new ComplexForm(1), 2, 2);
    }

    @Test
    public void testDetEigenRandom(){
        int success = 0;
        int fail = 0;
        int excepCount = 0;
        for (int i = 0; i < 100; i ++){
            int n = 3;
            Matrix A = new Matrix(n, n);
            for (int z = 0; z < n; z++){
                for (int y = 0; y < n; y++)
                    A.setElement(new ComplexForm(Math.random()*100-50, Math.random()*100-50), z, y);
            }
            /*
            A.setElement(new ComplexForm(-3, -2), 0, 0);
            A.setElement(new ComplexForm(-3, 4), 0, 1);
            A.setElement(new ComplexForm(4, -3), 1, 0);
            A.setElement(new ComplexForm(-3, -5), 1, 1);
            */
            try {
                ArrayList<Scalar> eigen = A.eigenValue();
                for (int j = 0; j < eigen.size(); j++) {
                    Matrix I = new Matrix(n, n);
                    I = I.scalarMult(eigen.get(j).getElement(0, 0));
                    I = I.scalarMult(new ComplexForm(-1));
                    if (A.add(I).det().magnitude() < 1) {
                        success++;
                    } else {
                        fail++;
                        System.out.println(A.add(I).det().magnitude());
                    }
                }
                if (eigen.size() != n){
                    System.out.print(A);
                    //fail();
                }
            } catch (ArithmeticException e){
                excepCount++;
            }
        }
        System.out.println(success +", " + fail + ", " + excepCount);
    }

    @Test
    public void testHermitian(){
        Matrix x = new Matrix(2, 1);
        x.setElement(new ComplexForm(3), 0, 0);
        x.setElement(new ComplexForm(2), 1, 0);
        Matrix y = new Matrix(2, 1);
        y.setElement(new ComplexForm(4), 0, 0);
        y.setElement(new ComplexForm(-2), 1, 0);
        assertEquals(new ComplexForm(8), x.innerProd(x,y));

        Matrix F = new Matrix(2, 1);
        F.setElement(new ComplexForm(2,-1),0,0);
        F.setElement(new ComplexForm(4,1),1,0);
        Matrix G = new Matrix(2,1);
        G.setElement(new ComplexForm(0,3),0,0);
        G.setElement(new ComplexForm(2,2),1,0);
        assertEquals(new ComplexForm(7,12), F.innerProd(F,G));
    }

    @Test
    public void testEVec(){
        int success = 0;
        int fail = 0;
        for (int G = 0; G < 1; G++) {
            int n = 5;
            Matrix A = new Matrix(n, n);
            for (int u = 0; u < n; u++){
                for (int p = 0; p < n; p++){
                    A.setElement(new ComplexForm((int)(Math.random()*10)-5, 0), u, p);
                }
            }
            if (A.det().magnitude() > 1e-10) {
/*
                A = new Matrix(3, 3);
                A.setElement(new ComplexForm(-2), 0, 1);
                A.setElement(new ComplexForm(4), 0, 2);
                A.setElement(new ComplexForm(-3), 1, 0);
                A.setElement(new ComplexForm(2), 1, 1);
                A.setElement(new ComplexForm(-4), 1, 2);
                A.setElement(new ComplexForm(-1), 2, 0);
                A.setElement(new ComplexForm(1), 2, 1);
                A.setElement(new ComplexForm(-5), 2, 2);
*/
                System.out.println(A);
                ArrayList<Scalar> eigenvalues = A.eigenValue();
                ArrayList<Matrix> eigenvectors = A.eigenVector();
                for (int i = 0; i < eigenvalues.size(); i++) {
                    Matrix A2 = A.add(new Matrix(n, n).scalarMult(ComplexForm.mult(new ComplexForm(-1), eigenvalues.get(i).getElement(0, 0))));
                    if(A2.mult(eigenvectors.get(i)).equals(new Matrix(n, 1)))
                        System.out.println("Success!");
                    else {
                        System.out.println("Failure!" + eigenvalues.get(i).getElement(0, 0).getPrettyString() + ", " + eigenvectors.get(i));
                    }
                }
            }
        }
    }

    @Test
    public void testPrettyPrint(){
        ComplexForm cf = new ComplexForm(365345.00783, 0.00002313);
        assertEquals(cf.getPrettyString(), "3.65E5+i2.31E-5");
        ComplexForm df = new ComplexForm(1, 1);
        assertEquals(df.getPrettyString(), "1+i");
        ComplexForm ef = new ComplexForm(0, -1);
        assertEquals(ef.getPrettyString(), "-i");
        ComplexForm ff = new ComplexForm(-323, 255);
        assertEquals(ff.getPrettyString(), "-323+i255");
    }
}