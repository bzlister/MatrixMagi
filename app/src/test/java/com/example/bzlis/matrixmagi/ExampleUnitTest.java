package com.example.bzlis.matrixmagi;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

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
    public void testLS(){
        Matrix A = new Matrix(2, 2);
        Matrix B = new Matrix(2, 3);
        A.setElement(new ComplexForm(1), 0, 0);
        A.setElement(new ComplexForm(2), 0, 1);
        A.setElement(new ComplexForm(3), 1, 0);
        A.setElement(new ComplexForm(4), 1, 1);
        B.setElement(new ComplexForm(9), 0, 0);
        B.setElement(new ComplexForm(12), 0, 1);
        B.setElement(new ComplexForm(15), 0, 2);
        B.setElement(new ComplexForm(19), 1, 0);
        B.setElement(new ComplexForm(26), 1, 1);
        B.setElement(new ComplexForm(33), 1, 2);
        Matrix X = A.leastSquares(B);
        System.out.println(A.mult(X));
        System.out.println(B);
        assertEquals(A.mult(X), B);
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
                    A.setElement(new ComplexForm(Math.random() * 100 - 50, 0), k, j);
                for (int p = 0; p < o; p++)
                    X.setElement(new ComplexForm(Math.random() * 100 - 50, 0), j, p);
            }
            try {
                Matrix X2 = A.leastSquares(A.mult(X));
                assert(X2.getError() < 1e-10);
            } catch (IllegalArgumentException e) {
                fail("Beeeg Yoshi");
            }
        }
        assert(true);
    }

    @Test
    public void LS1(){
        Matrix A = new Matrix(1, 3);
        Matrix B = new Matrix(1, 1);
        A.setElement(new ComplexForm(5.35), 0, 0);
        A.setElement(new ComplexForm(-36.36), 0, 1);
        A.setElement(new ComplexForm(7.36), 0, 2);
        B.setElement(new ComplexForm(534.71), 0, 0);
        Matrix T = A.transpose();
        T.mult(A.mult(T).inverse()).mult(B);
        assert(true);
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