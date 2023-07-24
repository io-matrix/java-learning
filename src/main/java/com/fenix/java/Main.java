package com.fenix.java;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        long a = 1 << 10;
        a = a << 10;
        System.out.println(a);

    }

    public int lengthOfLongestSubstring(String s) {

        return 0;
    }

    public static String longestPalindrome(String s) {

        // String s = "baaabad";
        String result = "";

        if (s.length() == 1) {
            return s;
        }

        char[] chars = s.toCharArray();
        if (s.length() == 2) {
            if (chars[0] == chars[1]) {
                return s;
            } else {
                return String.valueOf(chars[0]);
            }
        }

        for (int i = 1; i < chars.length - 1; i++) {
            int headCount;
            int endCount;
            int head;
            int end;
            String tmp = String.valueOf(chars[i]);

            if (chars[i] == chars[i - 1]) {
                tmp = chars[i - 1] + tmp;
                headCount = 2;
                endCount = 1;
                head = i - headCount;
                end = i + endCount;
                result = getString(result, chars, i, headCount, endCount, head, end, tmp);
            }

            tmp = String.valueOf(chars[i]);
            if (chars[i] == chars[i + 1]) {
                tmp = tmp + chars[i + 1];
                headCount = 1;
                endCount = 2;
                head = i - headCount;
                end = i + endCount;
                result = getString(result, chars, i, headCount, endCount, head, end, tmp);
            }

            tmp = String.valueOf(chars[i]);
            if (chars[i - 1] == chars[i + 1]) {
                tmp = chars[i - 1] + tmp + chars[i + 1];
                headCount = 2;
                endCount = 2;
                head = i - headCount;
                end = i + endCount;
                result = getString(result, chars, i, headCount, endCount, head, end, tmp);
            }
        }
        System.out.println(result);
        return result;
    }

    private static String getString(String result, char[] chars, int i, int headCount, int endCount, int head, int end,
            String tmp) {
        while (head >= 0 && end < chars.length) {
            if (chars[head] == chars[end]) {
                tmp = chars[head] + tmp + chars[end];
                headCount++;
                endCount++;
                head = i - headCount;
                end = i + endCount;
            } else {
                break;
            }
        }
        if (tmp.length() > result.length()) {
            result = tmp;
        }
        return result;
    }

}
