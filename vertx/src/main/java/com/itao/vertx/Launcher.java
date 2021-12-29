package com.itao.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itao.vertx.config.ConfigVerticle;
import com.itao.vertx.proxy1.ProxyVerticle;
import com.itao.vertx.web.SubrouterVerticle;
import com.itao.vertx.web.TemplateVerticle;
import com.itao.vertx.webclient.HttpsVerticle;
import io.vertx.core.json.jackson.DatabindCodec;

public class Launcher extends io.vertx.core.Launcher {

  public static void main(String[] args) {
    // java 中时间类型与数据库中datetime类型映射（vertx 不支持 Date类型的映射）
    ObjectMapper mapper = DatabindCodec.mapper();
    mapper.registerModule(new JavaTimeModule());
    new Launcher().dispatch(new String[]{"run", ConfigVerticle.class.getName()/*,"-instances","3"*/});
  }
}
