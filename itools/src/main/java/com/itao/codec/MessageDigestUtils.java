package com.itao.codec;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MessageDigestUtils {

    private final static String ALGORITHM_MD5 = "md5";
    private final static String ALGORITHM_SHA1 = "sha1";
    private final static String ALGORITHM_SHA256 = "sha256";
    private final static String ALGORITHM_SHA512 = "sha512";

    /**
     * 对字符串md5加密
     *
     * @param content 要加密的字符串
     */
    public static String encodeMd5(String content) {
        return encode(content, ALGORITHM_MD5);
    }

    /**
     * 对字节数组md5加密
     *
     * @param content 要加密的字符串
     */
    public static String encodeMd5(byte[] content) {
        return encode(content, ALGORITHM_MD5);
    }

    /**
     * 对字符串sha256加密
     *
     * @param content 要加密的字符串
     */
    public static String encodeSha256(String content) {
        return encode(content, ALGORITHM_SHA256);
    }

    /**
     * 对自己数组sha256加密
     *
     * @param content 要加密的字符串
     */
    public static String encodeSha256(byte[] content) {
        return encode(content, ALGORITHM_SHA256);
    }

    /**
     * 对字符串sha1加密
     *
     * @param content 要加密的字符串
     */
    public static String encodeSha1(String content) {
        return encode(content, ALGORITHM_SHA1);
    }

    /**
     * 对自己数组sha1加密
     *
     * @param content 要加密的字符串
     */
    public static String encodeSha1(byte[] content) {
        return encode(content, ALGORITHM_SHA1);
    }

    /**
     * 对字符串sha512加密
     *
     * @param content 要加密的字符串
     */
    public static String encodeSha512(String content) {
        return encode(content, ALGORITHM_SHA512);
    }

    /**
     * 对字节数组sha512加密
     *
     * @param content 要加密的字符串
     */
    public static String encodeSha512(byte[] content) {
        return encode(content, ALGORITHM_SHA512);
    }

    /**
     * 对字符串加密
     *
     * @param content 要加密的字符串
     */
    public static String encode(String content, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(content.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对字节数组加密
     *
     * @param content 要加密的字符串
     */
    public static String encode(byte[] content, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(content);
            return Hex.encodeHexString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(encodeSha512("sjt"));
    }
}
