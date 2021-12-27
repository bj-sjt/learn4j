package com.itao.vertx.webclient;

import com.itao.vertx.util.MediaTypeUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultipartFormVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var webClient = WebClient.create(vertx);
    var mediaType = MediaTypeUtil.mediaType("shicgf.png");
    log.info("media type:{}",mediaType);
    MultipartForm form = MultipartForm.create()
      .attribute("imageDescription", "a very nice image")
      .binaryFileUpload(
        "imageFile",
        "shicgf.png",
        "C:\\Users\\泰克贝思\\Pictures\\Saved Pictures\\shicgf.png",
        mediaType
        );

    // 提交multipart form表单
    webClient
      .post(8080, "localhost", "/multipart/form")
      .sendMultipartForm(form)
      .onSuccess(res -> {
        log.info(res.bodyAsString());
      })
      .onFailure(System.out::println);
  }
}
