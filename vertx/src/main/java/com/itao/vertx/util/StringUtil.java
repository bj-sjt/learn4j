package com.itao.vertx.util;

public class StringUtil {

  public static final char UNDERLINE = '_';

  /**
   * 驼峰格式字符串转换为下划线格式字符串
   *
   * @param param 驼峰格式字符串
   * @return 下划线格式字符串
   */
  public static String camelToUnderline(String param) {
    if (param == null || "".equals(param.trim())) {
      return "";
    }
    int len = param.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char c = param.charAt(i);
      if (Character.isUpperCase(c)) {
        sb.append(UNDERLINE);
        sb.append(Character.toLowerCase(c));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * 下划线格式字符串转换为驼峰格式字符串
   *
   * @param param 下划线格式字符串
   * @return 驼峰格式字符串
   */
  public static String underlineToCamel(String param) {
    if (param == null || "".equals(param.trim())) {
      return "";
    }
    int len = param.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char c = param.charAt(i);
      if (c == UNDERLINE) {
        if (++i < len) {
          sb.append(Character.toUpperCase(param.charAt(i)));
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * 判断一个字符串是否是 null 或 "" 或 "  "
   * @param text 指定的字符串
   * @return true or false
   */
  public boolean isNotBlank(String text) {
    return text != null && "".equals(text.trim());
  }

}
