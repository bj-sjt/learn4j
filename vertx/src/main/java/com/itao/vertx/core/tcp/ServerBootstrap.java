package com.itao.vertx.core.tcp;


public class ServerBootstrap extends io.vertx.core.Launcher{

  public static void main(String[] args) {
    new ServerBootstrap().dispatch(new String[]{"run", TcpServerVerticle.class.getName()/*,"-instances","3"*/});
  }
}
