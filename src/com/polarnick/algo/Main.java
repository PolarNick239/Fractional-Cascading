package com.polarnick.algo;

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
public class Main {

    public static void main(String[] args) throws Exception {
        try (BufferedReader in = new BufferedReader(new FileReader("permutation.in"));
             PrintWriter out = new PrintWriter("permutation.out")) {
            while (true) {
                StringTokenizer tok = new StringTokenizer(in.readLine());
                int n = Integer.parseInt(tok.nextToken());
                int m = Integer.parseInt(tok.nextToken());
                int[] a = new int[n];
                tok = new StringTokenizer(in.readLine());
                for (int i = 0; i < n; i++) {
                    a[i] = Integer.parseInt(tok.nextToken());
                }
                RangeTree tree = new FCRangeTree(a);
                for (int i = 0; i < m; i++) {
                    tok = new StringTokenizer(in.readLine());
                    int l = Integer.parseInt(tok.nextToken());
                    int r = Integer.parseInt(tok.nextToken());
                    int min = Integer.parseInt(tok.nextToken());
                    int max = Integer.parseInt(tok.nextToken());
                    out.println(tree.count(l, r + 1, min, max));
                }

                // Multi-tests separated by empty line
                if (in.readLine() == null) {
                    break;
                }
            }
        }
    }

}
