package com.itao.codec;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;

public class Main {
    public static void main(String[] args) {
        String key = "1234567890abcdef";
        byte[] encrypt = SecureUtil.aes(key.getBytes()).encrypt("abc".getBytes());
        String hex = SecureUtil.aes(key.getBytes()).encryptHex("abc");
        String base64 = SecureUtil.aes(key.getBytes()).encryptBase64("abc");
//        System.out.println(base64);
//        System.out.println(SecureUtil.aes(key.getBytes()).decryptStr(hex));
//        System.out.println(SecureUtil.aes(key.getBytes()).decryptStr(base64));
//        System.out.println(SecureUtil.aes(key.getBytes()).decryptStr(encrypt));

        System.out.println(DigestUtil.md5Hex16("abc"));
        System.out.println(DigestUtil.md5Hex("abc"));
    }
}
