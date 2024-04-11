package jav.fenix;

public class Main {

    static float BASE = 100000F;

    public static void main(String[] args) {

        int orderId = 38;
        long space = 17500;
        for (int i = 0; i < 40; i++) {
            long totalSpace = space * 1024 * 1024 * 1024 * 1024;
            System.out.println("update ehl_order set product_spec_id = '2106181030300449159' WHERE order_id = 'KROF202307270"+orderId+"';");
            orderId -= 1;
            space -= 500;
        }

//        for (int i = 0; i < 10; i++) {
//            System.out.println(Math.random());
//            System.out.println(Math.random() * 9);
//            System.out.println(Math.pow(10, 5));
//            String code = String.valueOf((int) ((Math.random() * 9 + 1) * BASE));
//            System.out.println(code);
//            System.out.println("************************");
//        }
    }


    // static class OOMObject{}

    // public static void main(String[] args) throws InterruptedException {

    //     List<OOMObject> list = new ArrayList<>();

    //     while (true){
    //         list.add(new OOMObject());
    //         Thread.sleep(1000);
    //     }

    // }

    // public int lengthOfLongestSubstring(String s) {

    //     return 0;
    // }

    // public static String longestPalindrome(String s) {

    //     // String s = "baaabad";
    //     String result = "";

    //     if (s.length() == 1) {
    //         return s;
    //     }

    //     char[] chars = s.toCharArray();
    //     if (s.length() == 2) {
    //         if (chars[0] == chars[1]) {
    //             return s;
    //         } else {
    //             return String.valueOf(chars[0]);
    //         }
    //     }

    //     for (int i = 1; i < chars.length - 1; i++) {
    //         int headCount;
    //         int endCount;
    //         int head;
    //         int end;
    //         String tmp = String.valueOf(chars[i]);

    //         if (chars[i] == chars[i - 1]) {
    //             tmp = chars[i - 1] + tmp;
    //             headCount = 2;
    //             endCount = 1;
    //             head = i - headCount;
    //             end = i + endCount;
    //             result = getString(result, chars, i, headCount, endCount, head, end, tmp);
    //         }

    //         tmp = String.valueOf(chars[i]);
    //         if (chars[i] == chars[i + 1]) {
    //             tmp = tmp + chars[i + 1];
    //             headCount = 1;
    //             endCount = 2;
    //             head = i - headCount;
    //             end = i + endCount;
    //             result = getString(result, chars, i, headCount, endCount, head, end, tmp);
    //         }

    //         tmp = String.valueOf(chars[i]);
    //         if (chars[i - 1] == chars[i + 1]) {
    //             tmp = chars[i - 1] + tmp + chars[i + 1];
    //             headCount = 2;
    //             endCount = 2;
    //             head = i - headCount;
    //             end = i + endCount;
    //             result = getString(result, chars, i, headCount, endCount, head, end, tmp);
    //         }
    //     }
    //     System.out.println(result);
    //     return result;
    // }

    // private static String getString(String result, char[] chars, int i, int headCount, int endCount, int head, int end,
    //         String tmp) {
    //     while (head >= 0 && end < chars.length) {
    //         if (chars[head] == chars[end]) {
    //             tmp = chars[head] + tmp + chars[end];
    //             headCount++;
    //             endCount++;
    //             head = i - headCount;
    //             end = i + endCount;
    //         } else {
    //             break;
    //         }
    //     }
    //     if (tmp.length() > result.length()) {
    //         result = tmp;
    //     }
    //     return result;
    // }

}
