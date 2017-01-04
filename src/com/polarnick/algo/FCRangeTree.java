package com.polarnick.algo;

public class FCRangeTree implements RangeTree {

    private static final int MAX_INT_ARRAY_SIZE = 1000_000;

    private final Node root;

    private final int[] linksToRoot;
    private final int minValue;
    private final int maxValue;

    /**
     * @param a can contain any integer values (with collisions, negatives and big values)
     */
    FCRangeTree(int[] a) {
        Node[] curLevelNodes = new Node[a.length];
        int minValue = a[0];
        int maxValue = a[0];
        for (int i = 0; i < a.length; i++) {
            curLevelNodes[i] = new Node(a[i], i + 1);
            minValue = Math.min(minValue, a[i]);
            maxValue = Math.max(maxValue, a[i]);
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        int n = a.length;
        while (n > 1) {
            for (int i = 0; 2 * i + 1 < n; i++) {
                curLevelNodes[i] = new Node(curLevelNodes[2 * i], curLevelNodes[2 * i + 1]);
            }
            int m = n / 2;
            if (n % 2 == 1) {
                curLevelNodes[m] = curLevelNodes[n - 1];
                m++;
            }
            n = m;
        }
        this.root = curLevelNodes[0];

        int maxNormalizedValue = maxValue - minValue;
        if (MAX_INT_ARRAY_SIZE >= maxNormalizedValue) {
            this.linksToRoot = calculateLinksFromRange(minValue, maxValue, root.sortedValues);
        } else {
            this.linksToRoot = null;
        }
    }

    @Override
    public int count(int leftIndex, int rightIndex, int minValue, int maxValue) {
        if (leftIndex >= rightIndex || minValue > maxValue || minValue > this.maxValue || maxValue < this.minValue) {
            return 0;
        }
        minValue = Math.max(this.minValue, minValue) - this.minValue;
        maxValue = Math.min(this.maxValue, maxValue) - this.minValue;

        final int minIndexInRoot;// inclusive
        final int maxIndexInRoot;// exclusive
        if (this.linksToRoot == null) {
            throw new UnsupportedOperationException("Binary search for case with too big values currently not supported!");
        } else {
            minIndexInRoot = linksToRoot[minValue];
            maxIndexInRoot = linksToRoot[maxValue + 1];
        }

        return root.count(leftIndex, rightIndex, minIndexInRoot, maxIndexInRoot);
    }

    private static class Node {
        final int leftIndex;// inclusive
        final int rightIndex;// exclusive
        final int[] sortedValues;
        final Node leftChild;
        final Node rightChild;
        final int[] indexToNotLesserInLeft;
        final int[] indexToNotLesserInRight;

        Node(int value, int index) {
            this.leftIndex = index;
            this.rightIndex = index + 1;
            this.sortedValues = new int[]{value};
            this.leftChild = null;
            this.rightChild = null;
            this.indexToNotLesserInLeft = null;
            this.indexToNotLesserInRight = null;
        }

        Node(Node leftChild, Node rightChild) {
            this.leftIndex = leftChild.leftIndex;
            this.rightIndex = rightChild.rightIndex;
            this.sortedValues = merge(leftChild.sortedValues, rightChild.sortedValues);
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.indexToNotLesserInLeft = calculateLinks(sortedValues, leftChild.sortedValues);
            this.indexToNotLesserInRight = calculateLinks(sortedValues, rightChild.sortedValues);
        }

        int count(int l, int r, int fromI, int toI) {
            if (l >= r || fromI > toI) {
                throw new IllegalStateException("Wrong range queried!");
            }
            if (fromI == toI) {
                return 0;
            }
            if (l <= leftIndex && rightIndex <= r) {
                return toI - fromI;
            }
            if (r <= leftIndex || rightIndex <= l) {
                return 0;
            }
            int count = leftChild.count(l, r, indexToNotLesserInLeft[fromI], indexToNotLesserInLeft[toI])
                    + rightChild.count(l, r, indexToNotLesserInRight[fromI], indexToNotLesserInRight[toI]);
            return count;
        }
    }

    private static int[] merge(int[] xs, int[] ys) {
        int xi = 0;
        int yi = 0;
        int[] rs = new int[xs.length + ys.length];
        int ri = 0;
        while (xi < xs.length || yi < ys.length) {
            while (xi < xs.length && (yi == ys.length || xs[xi] <= ys[yi])) {
                rs[ri] = xs[xi++];
                ++ri;
            }
            while (yi < ys.length && (xi == xs.length || ys[yi] <= xs[xi])) {
                rs[ri] = ys[yi++];
                ++ri;
            }
        }
        return rs;
    }

    private static int[] calculateLinks(int[] values, int[] childValues) {
        int[] links = new int[values.length + 1];
        int next = 0;
        for (int i = 0; i < values.length; i++) {
            while (next < childValues.length && childValues[next] < values[i]) {
                ++next;
            }
            links[i] = next;
        }
        links[values.length] = childValues.length;// last element is fictive
        return links;
    }

    private static int[] calculateLinksFromRange(int minValue, int maxValue, int[] childValues) {
        int countOfDifferentValues = maxValue - minValue + 1;
        int[] links = new int[countOfDifferentValues + 1];
        int next = 0;
        for (int i = 0; i < countOfDifferentValues; i++) {
            while (next < childValues.length && childValues[next] < minValue + i) {
                ++next;
            }
            links[i] = next;
        }
        links[countOfDifferentValues] = childValues.length;// last element is fictive
        return links;
    }
}