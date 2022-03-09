package com.itao;

public class IdVerify {
    private static final int[] weight = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10 ,5, 8, 4, 2};
    private static final String[] checkCode = new String[]{"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"};

    public static boolean verify(String idCard) {
        char[] array = idCard.toCharArray();
        int sum = 0;
        for (int i = 0; i < array.length - 1; i++) {
            sum += weight[i] * Integer.parseInt(String.valueOf(array[i]));
        }
        int mod = Math.floorMod(sum, 11);
        String code = checkCode[mod];
        return code.equals(idCard.substring(17));
    }

}
