package com.itao;

public class Main {

    public static void main(String[] args) {
        // 原码   1000 0000 0000 0000 0000 0000 0000 0001
        // 反码   1111 1111 1111 1111 1111 1111 1111 1110
        // 补码   1111 1111 1111 1111 1111 1111 1111 1111
        // 计算机中以补码形式存储

        System.out.println(Integer.toBinaryString(-1));
    }

    private static void chineseWord() {
        // 中文对应的unicod编码的范围： \u4e00 - \u9fa5
        int i=1;
        StringBuilder sb = new StringBuilder();
        for(char c='\u4e00'; c<='\u9fa5';c++,i++){
            sb.append(c);
            if(i %50 ==0){
                sb.append("\n");
            }
        }
        System.out.print(sb.length());
    }
}
