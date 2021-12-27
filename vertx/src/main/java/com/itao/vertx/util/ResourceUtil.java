package com.itao.vertx.util;

import java.io.File;
import java.net.URL;

public class ResourceUtil {

  public static String classPath(Class<?> clazz,String path){
    URL url = clazz.getClassLoader().getResource(path);
    if(url == null){
      throw new RuntimeException("classpath is null");
    }
    return url.getPath();
  }

}
