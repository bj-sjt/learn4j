package com.itao.vertx.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtil {

    public static String getLocalIp(){
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
        return addr.getHostAddress();
    }
}
