import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Решение является примером использования Range Tree с Fractional Cascading.
 * Задача взята с http://contest2.yandex.ru/contest/472/problems/16E/
 *
 *
 * Условие задачи:
 *
 * Имя входного файла:      permutation.in
 * Имя выходного файла:     permutation.out
 * Ограничение по времени:  1 секунда
 * Ограничение по памяти:   256 мегабайт
 *
 * Вася выписал на доске в каком-то порядке все числа от 1 до N, каждое число ровно по одному разу.
 * Количество чисел оказалось довольно большим, поэтому Вася не может окинуть взглядом все числа.
 * Однако ему надо все-таки представлять эту последовательность, поэтому он написал программу,
 * которая отвечает на вопрос - сколько среди чисел, стоящих на позициях с x по y, по величине лежат в интервале от k до l.
 * Сделайте то же самое.
 *
 * Формат входных данных:
 *
 * В первой строке лежит два натуральных числа - 1 <= N <= 100 000 - количество чисел, которые выписал Вася
 * и 1 <= M <= 100 000 - количество вопросов, которые Вася хочет задать программе.
 * Во второй строке дано N чисел - последовательность чисел, выписанных Васей.
 * Далее в M строках находятся описания вопросов.
 * Каждая строка содержит четыре целых числа 1 <= x <= y <= N и 1 <= k <= l <= N.
 *
 * Формат выходных данных:
 *
 * Выведите M строк, каждая должна содержать единственное число - ответ на Васин вопрос.
 *
 * Примеры:
 *
 * permutation.in               permutation.out
 * 4 2
 * 1 2 3 4
 * 1 2 2 3                      1
 * 1 3 1 3                      3
 *
 * 11 3
 * 5 9 8 7 1 2 6 11 3 10 4
 * 2 10 2 7                     4
 * 4 8 1 11                     5
 * 5 7 3 3                      0
 *
 * @author Polyarnyi Nikolay
 */
public class Problem16E {

    public static void main(String[] args) throws Exception {
        try (BufferedReader in = new BufferedReader(new FileReader("permutation.in"));
             PrintWriter out = new PrintWriter("permutation.out")) {
            StringTokenizer tok = new StringTokenizer(in.readLine());
            int n = Integer.parseInt(tok.nextToken());
            int m = Integer.parseInt(tok.nextToken());
            int[] a = new int[n];
            tok = new StringTokenizer(in.readLine());
            for (int i = 0; i < n; i++) {
                a[i] = Integer.parseInt(tok.nextToken());
            }
            PermutationRangeTree tree = new PermutationRangeTree(a);
            for (int i = 0; i < m; i++) {
                tok = new StringTokenizer(in.readLine());
                int l = Integer.parseInt(tok.nextToken());
                int r = Integer.parseInt(tok.nextToken());
                int min = Integer.parseInt(tok.nextToken());
                int max = Integer.parseInt(tok.nextToken());
                out.println(tree.count(l, r + 1, min, max));
            }
        }
    }

    private static class PermutationRangeTree {

        private static final int MAX_INT_ARRAY_SIZE = 1000_000;

        private final Node root;

        private final int[] linksToRoot;
        private final int minValue;
        private final int maxValue;

        /**
         * @param a can contain any integer values (with collisions, negatives and big values)
         */
        PermutationRangeTree(int[] a) {
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

        /**
         * @param leftIndex  left index of search range (inclusive)
         * @param rightIndex right index of search range (exclusive)
         * @param minValue   minimum value to search in range
         * @param maxValue   maximum value to search in range
         * @return count of such {@code a[i]}, that {@code leftIndex <= i < rightIndex}
         * and {@code minValue <= a[i] <= maxValue}
         */
        public int count(int leftIndex, int rightIndex, int minValue, int maxValue) {
            if (leftIndex >= rightIndex || minValue > maxValue) {
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

}
