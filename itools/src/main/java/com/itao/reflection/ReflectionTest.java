package com.itao.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ReflectionTest {
    public void test(String str1,String str2){
        System.out.println(str1+str2);
    }

    public static void main(String[] args) throws NoSuchMethodException{
        Method test = ReflectionTest.class.getMethod("test", String.class, String.class);
        for(Parameter parameter :test.getParameters()){
            System.out.println(parameter);
        }
    }
}
