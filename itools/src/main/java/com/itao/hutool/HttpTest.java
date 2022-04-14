package com.itao.hutool;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

public class HttpTest {

    public static void main(String[] args) {
        //System.out.println(HttpUtil.get("http://webmagic.io/docs/zh/"));
        HttpRequest request = HttpUtil.createRequest(Method.GET, "http://webmagic.io/docs/zh/");
        System.out.println(request.execute().body());
    }
}
