package com.polarnick.algo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public abstract class RangeTreeTestsBase {

    protected abstract RangeTree create(int[] xs);

    @Test
    public void testSimpleCase1() throws Exception {
        RangeTree tree = create(new int[]{1, 2, 3, 4, 5});
        assertEquals(5, tree.count(1, 6, 1, 5));

        assertEquals(1, tree.count(1, 2, 1, 1));

        assertEquals(1, tree.count(1, 3, 1, 1));
        assertEquals(2, tree.count(1, 3, 1, 2));

        assertEquals(1, tree.count(1, 4, 3, 3));
        assertEquals(2, tree.count(1, 4, 2, 3));

        assertEquals(0, tree.count(10, 10, 1, 5));
        assertEquals(0, tree.count(-10, -10, 1, 5));
    }
}