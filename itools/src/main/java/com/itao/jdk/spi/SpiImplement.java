package com.itao.jdk.spi;

public class SpiImplement implements SpiInterface{
    @Override
    public void test() {
        System.out.println("spi 测试");
    }
}
