package com.polarnick.algo;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class StressTest {

    Map<String, RangeTree> createImplementations(int[] xs) {
        Map<String, RangeTree> implementations = new HashMap<>();
        implementations.put("Naive", new NaiveRangeTree(xs));
        implementations.put("FractionalCascaded", new FCRangeTree(xs));
        return implementations;
    }

    @Test
    public void testCorrectness() {
        int testsNumber = 1000;
        int maxN = 1000;
        int maxM = 1000;

        testStress(testsNumber, maxN, maxM);
    }

    @Test
    public void testPerformance() {
        int testsNumber = 10;
        int maxN = 100000;
        int maxM = 100000;

        testStress(testsNumber, maxN, maxM);
    }

    private void testStress(int testsNumber, int maxN, int maxM) {
        Random r = new Random(239);
        for (int test = 1; test <= testsNumber; ++test) {
            int n = 1 + r.nextInt(maxN);
            int m = 1 + r.nextInt(maxM);
            System.out.println("___Test #" + test + ": n=" + n + " m=" + m + "___");

            int[] xs = new int[n];
            for (int i = 0; i < n; ++i) {
                xs[i] = r.nextInt(n);
            }

            int[] input = new int[m * 4];
            for (int i = 0; i < m; ++i) {
                int from = 1 + r.nextInt(n);
                int to = from + r.nextInt(n - from + 1);
                int min = r.nextInt(n);
                int max = r.nextInt(n);
                if (min > max) {
                    int tmp = min;
                    min = max;
                    max = tmp;
                }
                if (min == max) {
                    max++;
                }
                input[4 * i + 0] = from;
                input[4 * i + 1] = to;
                input[4 * i + 2] = min;
                input[4 * i + 3] = max;
            }

            List<Integer> results = new ArrayList<>(m);
            Map<String, RangeTree> implementations = createImplementations(xs);
            for (Map.Entry<String, RangeTree> entry: implementations.entrySet()) {
                String name = entry.getKey();
                RangeTree tree = entry.getValue();

                long start = System.currentTimeMillis();
                for (int i = 0; i < m; ++i) {
                    int from = input[4 * i + 0];
                    int to = input[4 * i + 1];
                    int min = input[4 * i + 2];
                    int max = input[4 * i + 3];
                    int res = tree.count(from, to, min, max);
                    if (results.size() < i + 1) {
                        results.add(res);
                    } else {
                        assertEquals((int) results.get(i), res);
                    }
                }
                long passed = System.currentTimeMillis() - start;
                System.out.println(name + ": " + passed + " ms");
            }
        }
    }

}
