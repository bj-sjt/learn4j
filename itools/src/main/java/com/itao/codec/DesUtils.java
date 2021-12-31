package com.itao.codec;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class DesUtils {
    private final static String ALGORITHM_DES = "des";
    private final static String DES_KEY = "12345678"; //des 加密的秘钥必须是8位


    /**
     * 对字符串进行des加密后在进行base64加密
     *
     * @param content 要加密的字符串
     */
    public static String encode(String content) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] doFinal = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对字符串进行des加密后在进行base64加密
     *
     * @param content 要加密的字符串
     */
    public static String encode(byte[] content) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] doFinal = cipher.doFinal(content);
            return Base64.encodeBase64String(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将密文解密成明文
     *
     * @param base 密文
     * @return 明文
     */
    public static String decode(String base) {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] bytes = Base64.decodeBase64(base);
            byte[] doFinal = cipher.doFinal(bytes);
            return new String(doFinal, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将密文解密成明文
     *
     * @param base 密文
     * @return 明文
     */
    public static byte[] decode2Bytes(String base) {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] bytes = Base64.decodeBase64(base);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取Cipher对象
     */
    private static Cipher getCipher(int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
        SecretKeySpec secretKeySpec = new SecretKeySpec(DES_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM_DES);
        cipher.init(mode, secretKeySpec);
        return cipher;
    }

    public static void main(String[] args) {
        System.out.println(decode("zLegYB8hZwE="));
    }
}
