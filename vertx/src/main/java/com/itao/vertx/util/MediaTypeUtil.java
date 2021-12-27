package com.itao.vertx.util;

import org.apache.tika.Tika;

public class MediaTypeUtil {
  public static String mediaType(String FileName) {
    var tika = new Tika();
    return tika.detect(FileName);
  }
  
  public static void main(String[] args) {
    System.out.println(mediaType("1.jpg"));
  }
}
