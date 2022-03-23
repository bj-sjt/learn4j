package com.itao.qrcode;

import cn.hutool.extra.qrcode.QrCodeUtil;

import java.io.File;

public class QrCode {

    public static void main(String[] args) {
        QrCodeUtil.generate("http://www.baidu.com", 500, 500, new File("D:\\itchat4j\\login\\qr.jpg"));
    }
}
