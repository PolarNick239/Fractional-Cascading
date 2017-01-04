package com.polarnick.algo;

public class NaiveRangeTree implements RangeTree {

    private final int[] a;

    public NaiveRangeTree(int[] a) {
        this.a = a;
    }

    @Override
    public int count(int leftIndex, int rightIndex, int minValue, int maxValue) {
        leftIndex = Math.max(0, leftIndex - 1);
        rightIndex = Math.min(a.length, rightIndex - 1);

        int number = 0;
        for (int i = leftIndex; i < rightIndex; ++i) {
            if (a[i] >= minValue && a[i] <= maxValue) {
                ++number;
            }
        }

        return number;
    }
}
