package com.polarnick.algo;

public interface RangeTree {

    /**
     * @param leftIndex  left index of search range (inclusive, indexing from 1)
     * @param rightIndex right index of search range (exclusive, indexing from 1)
     * @param minValue   minimum value to search in range
     * @param maxValue   maximum value to search in range
     * @return count of such {@code a[i]}, that {@code leftIndex <= i < rightIndex}
     * and {@code minValue <= a[i] <= maxValue}
     */
    int count(int leftIndex, int rightIndex, int minValue, int maxValue);

}
