package com.itao.io;

import cn.hutool.core.io.watch.SimpleWatcher;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatch {
    public static void main(String[] args) throws IOException, InterruptedException {
        WatchMonitor monitor = WatchUtil.create("C:\\Users\\32088\\Desktop\\1", 3, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
        monitor.watch(new SimpleWatcher(){
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                if (event.context() instanceof Path) {
                    Path path = (Path) event.context();
                    System.out.println(path);
                }
                System.out.println("onCreate..." + currentPath);
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                if (event.context() instanceof Path) {
                    Path path = (Path) event.context();
                    System.out.println(path);
                }
                System.out.println("onModify..." + currentPath);
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                if (event.context() instanceof Path) {
                    Path path = (Path) event.context();
                    System.out.println(path);
                }
                System.out.println("onDelete..." + currentPath);
            }
        });
    }
}
