package com.itao.jdk.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

@Slf4j
public class Server {
    public static void main(String[] args) {
        try {
            // 创建 ServerSocketChannel
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // 设置非阻塞模式
            ssc.configureBlocking(false);
            // 创建 Selector
            Selector selector = Selector.open();
            // 将 ssc 注册到 selector 中, 并监听 accept 事件
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            // 绑定端口
            ssc.bind(new InetSocketAddress(8080));
            // 阻塞当前线程 直到有感兴趣(accept、read、write、connect)的事件发生。connect为客户端事件
            while (true) {
                selector.select();
                // 当有感兴趣的事件发生时，会将这些 SelectionKey 添加到 selector.selectedKeys() 集合中
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // 当处理完感兴趣的事件后 selector不会主动将 SelectionKey 从 selector.selectedKeys() 删除
                    // 所以要手动删除(如果不删除，下次执行到这里到时候，会报 NullPointerException)
                    iterator.remove();
                    if (key.isAcceptable()) { // 是否为 accept 事件
                        // 获取连接的客户端的 SocketChannel， 并设置非阻塞模式
                        SocketChannel sc = ssc.accept();
                        log.info("客户端连接：{}", sc);
                        sc.configureBlocking(false);
                        // 将 sc 注册到 selector 中, 并监听 read 事件
                        sc.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        try {
                            // 获取发生可读事件的 SocketChannel
                            SocketChannel sc = (SocketChannel) key.channel();
                            // 创建一个16个字节大小的 ByteBuffer
                            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                            int read = sc.read(byteBuffer);
                            if (read == -1) {
                                throw new IOException();
                            }
                            log.info("读取到{}字节",read);
                        } catch (IOException e) {
                            e.printStackTrace();
                            key.cancel();
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
