package com.itao.vertx.mail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;

public class MailVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    MailConfig config = new MailConfig();
    config.setHostname("smtp.163.com");
    config.setSsl(true);
    config.setPort(465);
    config.setStarttls(StartTLSOptions.DISABLED);
    config.setUsername("itao008@163.com");
    config.setPassword("123445");
    //config.set
    config.setConnectTimeout(3000);
    MailClient mailClient = MailClient.createShared(vertx, config, "test");

    MailMessage message = new MailMessage();
    message.setFrom("itao008@163.com");//发送者的邮箱地址
    message.setTo("476004058@qq.com");//接收者的邮箱地址
    message.setCc("320885976@qq.com");//抄送的邮箱地址
    message.setSubject("vertx");
    message.setText("this is the plain message text");
    message.setHtml("this is html text <a href=\"https://vertx.io\">vertx.io</a>");

    mailClient.sendMail(message)
      .onSuccess(mr -> {
        String messageID = mr.getMessageID();
        System.out.println(messageID);
      })
      .onFailure(t -> {
        System.out.println(t.getMessage());
      });
  }
}
