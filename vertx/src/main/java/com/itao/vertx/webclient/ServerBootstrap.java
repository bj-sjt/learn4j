package com.itao.vertx.webclient;

public class ServerBootstrap extends io.vertx.core.Launcher{

  public static void main(String[] args) {
    new ServerBootstrap().dispatch(new String[]{"run", ServerVerticle.class.getName()/*,"-instances","3"*/});
  }
}
