package com.polarnick.algo;

public class FCRangeTreeTest extends RangeTreeTestsBase {
    @Override
    protected RangeTree create(int[] xs) {
        return new FCRangeTree(xs);
    }
}
