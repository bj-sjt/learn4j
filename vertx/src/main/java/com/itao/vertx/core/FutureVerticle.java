package com.itao.vertx.core;

import com.itao.vertx.util.ResourceUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;

import java.io.File;

public class FutureVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var fs = vertx.fileSystem();
        var classpath = ResourceUtil.classPath(FutureVerticle.class);
        var fooPath = classpath + File.separator + "foo.txt";
        var barPath = classpath + File.separator + "bar.txt";
        fs.createFile(fooPath)
                .compose(v -> fs.writeFile(fooPath, Buffer.buffer("Hello Vertx")))
                .compose(v -> fs.move(fooPath, barPath))
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        System.out.println("success");
                    } else {
                        System.out.println("fail");
                    }
                });
    }
}
