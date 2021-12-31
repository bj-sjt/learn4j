package com.itao.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class CglibProxyClass {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();     // 创建 Enhancer 实例
        enhancer.setSuperclass(Greeting.class);    // 设置父类
        //enhancer.setInterfaces(Runnable.class);               //设置接口

        //设置Callback
        enhancer.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> {
            System.out.println("invoke before");
            Object invokeSuper = proxy.invokeSuper(obj, args1);  // 调用父类的方法
            System.out.println("invoke after");
            return invokeSuper;
        });

        Greeting greeting = (Greeting) enhancer.create();   // 创建代理
        greeting.hello("Alice");    //会调用Callback具体实现类的方法

    }
}
