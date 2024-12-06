package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of SumMatrix.
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private static final String MSG = "The number of threads must be greater than or equal to 1";
    private final int nthread;

    /**
     * 
     * @param n
     *          no. of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(MSG);
        }
        this.nthread = n;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        for (final Worker w : workers) {
            w.start();
        }
        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix; //NOPMD
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                for (final double e : matrix[i]) {
                    this.res += e;
                }
            }
        }

        /**
         * Returns the result of summing up the doubles within the matrix.
         * 
         * @return the sum of every element in the matrix
         */
        public double getResult() {
            return this.res;
        }
    }
}
