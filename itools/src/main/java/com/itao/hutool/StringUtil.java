package com.itao.hutool;

import cn.hutool.core.util.StrUtil;

public class StringUtil {
    public static void main(String[] args) {
        String format = StrUtil.format("{}{}{}", "hello", " ", "world");
        System.out.println(format);
    }
}
