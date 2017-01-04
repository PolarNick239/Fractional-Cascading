package com.polarnick.algo;

public class NaiveRangeTreeTest extends RangeTreeTestsBase {
    @Override
    protected RangeTree create(int[] xs) {
        return new NaiveRangeTree(xs);
    }
}
