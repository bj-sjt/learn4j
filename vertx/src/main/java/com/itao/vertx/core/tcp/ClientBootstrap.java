package com.itao.vertx.core.tcp;

public class ClientBootstrap extends io.vertx.core.Launcher{

  public static void main(String[] args) {
    new ClientBootstrap().dispatch(new String[]{"run", TcpClientVerticle.class.getName()/*,"-instances","3"*/});
  }
}
