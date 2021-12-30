package com.itao.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {

    public static void main(String[] args) {
        try {
            // 获取 SocketChannel
            SocketChannel sc = SocketChannel.open();
            // 连接服务器
            sc.connect(new InetSocketAddress("localhost", 8080));
            System.out.println("connected"); //用于debug调试
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
