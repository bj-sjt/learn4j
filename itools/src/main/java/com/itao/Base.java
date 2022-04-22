package com.itao;

public class Base {

    public static final int a = 1;

    public static void main(String[] args) {
        System.out.println(m1());
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
