package com.itao;

public class Base {

    public static final int a = 1;

    public static void main(String[] args) {
        text();
    }

    public static void text(){
        String text = """
                  行宫
                  元稹 〔唐代〕
                  
                  寥落古行宫，宫花寂寞红。
                  白头宫女在，闲坐说玄宗。
                """;

        System.out.println(text);
    }

    public static int m1() {
        int i = 0;
        try {
            return ++i;
        } finally {
            System.out.println(--i);
        }
    }

    public static void m2() {
        int a = 0;
        System.out.println(++a);
        System.out.println(a++);
    }

    public Inner getInner(){
        return new Inner();
    }


    class Inner{
        private int num;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

}
