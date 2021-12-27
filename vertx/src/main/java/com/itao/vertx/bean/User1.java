package com.itao.vertx.bean;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.shareddata.Shareable;
import io.vertx.sqlclient.templates.annotations.RowMapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DataObject
@RowMapped
public class User1 implements Shareable {
  private String name;
  private int age;

  @Override
  public Shareable copy() {
    var user = new User1();
    user.setAge(this.getAge());
    user.setName(this.getName());
    return user;
  }
}
