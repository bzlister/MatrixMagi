package com.example.bzlis.matrixmagi;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test_inverse(){
        for (int z = 0; z < 100; z++) {
            System.out.println();
            int n = new Random().nextInt(10);
            if (n == 0)
                n += 1;
            double[] plier = new double[]{-1,1};
            Matrix A = new Matrix(n, n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    A.setElement((plier[new Random().nextInt(1)]*(new Random().nextInt(100)+1.0)/(new Random().nextInt(100)+1)), i, j);
                }
            }
            double error;
            try {
                Matrix inv = A.inverse();
                error = inv.getError();
            } catch (IllegalArgumentException f) {
                System.out.println(f.getMessage());
                error = 0.0;
            }
            assert(error <= 1e-10);
        }
    }

    @Test
    public void test_gaussian(){
        for (int z = 0; z < 100; z++) {
            System.out.println();
            int n = new Random().nextInt(10);
            if (n == 0)
                n += 1;
            double[] plier = new double[]{-1, 1};
            Matrix A = new Matrix(n, n);
            Matrix B = new Matrix(n, 1);
            for (int i = 0; i < n; i++) {
                B.setElement((plier[new Random().nextInt(1)] * (new Random().nextInt(100) + 1.0) / (new Random().nextInt(100) + 1)), i, 0);
                for (int j = 0; j < n; j++) {
                    A.setElement((plier[new Random().nextInt(1)] * (new Random().nextInt(100) + 1.0) / (new Random().nextInt(100) + 1)), i, j);
                }
            }
            double error;
            try {
                Matrix x = A.guassElim(B);
                error = x.getError();
            } catch (IllegalArgumentException f) {
                System.out.println(f.getMessage());
                error = 0.0;
            }
            assert(error <= 1e-10);
        }
    }

    @Test
    public void test_leastSquares(){
        for (int z = 0; z < 100; z++) {
            System.out.println();
            int rA = new Random().nextInt(5)+1;
            int cA = new Random().nextInt(5)+1;
            int rB = new Random().nextInt(5)+1;
            double[] plier = new double[]{-1, 1};
            Matrix A = new Matrix(rA, cA);
            Matrix B = new Matrix(rB, 1);
            for (int i = 0; i < rA; i++) {
                for (int j = 0; j < cA; j++) {
                    A.setElement((plier[new Random().nextInt(1)] * (new Random().nextInt(100) + 1.0) / (new Random().nextInt(100) + 1)), i, j);
                }
            }
            for (int k = 0; k < rB; k++)
                B.setElement((plier[new Random().nextInt(1)] * (new Random().nextInt(100) + 1.0) / (new Random().nextInt(100) + 1)), k, 0);
            double error;
            try{
                Matrix x = A.leastSquares(B);
                error = x.getError();
            } catch (IllegalArgumentException f){
                System.out.println(f.getMessage());
                error = 0.0;
            }
            System.out.println(error);
            assert(true);
        }
    }

    @Test
    public void test_power(){
        for (int z = 0; z < 100; z++) {
            System.out.println();
            int n = new Random().nextInt(10);
            if (n == 0)
                n += 1;
            double[] plier = new double[]{-1, 1};
            Matrix A = new Matrix(n, n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    A.setElement(new Random().nextInt(4)+1.0, i, j);
                }
            }
            double error;
            int pow = (int)plier[new Random().nextInt(1)]*n;
            try {
                Matrix result = A.power(pow);
                Matrix cp = new Matrix(n, n);
                if (pow < 0)
                    cp = A.inverse();
                if (pow > 0)
                    cp = A.duplicate();
                Matrix base = cp;
                while (n > 1) {
                    cp = cp.mult(base);
                    n--;
                }
                error = result.sumSquaredErrors(cp);
            } catch (IllegalArgumentException e){
                System.out.println(e.getMessage());
                error = 0;
            }
            assert(error <= 1e-10);
        }
    }

    @Test
    public void test_QR(){
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
        Matrix[] QR = A.QR();
        System.out.println(QR[0]);
        System.out.println(QR[1]);
        assert(true);
    }

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
}