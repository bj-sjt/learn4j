package com.itao.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class CglibProxyInterface {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();     // 创建 Enhancer 实例
        enhancer.setInterfaces(new Class[] {Runnable.class});  //设置接口

        //设置Callback
        enhancer.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> {
            System.out.println("invoke before");
            System.out.println(Thread.currentThread().getName());
            System.out.println("invoke after");
            return null;
        });

        Runnable runnable = (Runnable) enhancer.create();   // 创建代理
        new Thread(runnable, "CglibProxyInterface").start();
    }
}
