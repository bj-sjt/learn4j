package com.itao.jdk;

public class TestSwap {
    public static void main(String[] args) {
        // 两个相同的数 异或（^） 都为 0
        // 任何数与0 异或（^） 结果还是自己
        int a = 3;  // 0011
        int b = 5;  // 0101
        a = a ^ b;  // 0110
        b = a ^ b;  // 0011    a ^ b ^ b = a
        a = a ^ b;  // 0101    a ^ a ^ b = b
        System.out.println("a = " + a + "," + "b = " + b);


        int c = 3;
        int d = 5;
        c = c + d;
        d = c - d;
        c = c - d;
        System.out.println("c = " + c + "," + "d = " + d);
    }
}
