package com.gcy.mq.deduplication.tools.reflection;

import java.lang.reflect.Field;

public class ReflectionFields {

  public static Field[] getFields(Class<?> clazz) {
    return clazz.getDeclaredFields();
  }

  // 递归查找指定字段，可以查找到父类的字段
  public static Field getField(Class<?> clazz, String fieldName) {
    return getField(false, clazz, fieldName);
  }

  // 递归查找指定字段，可以查找到父类的字段
  public static Field getFieldIncludeParent(Class<?> clazz, String fieldName) {
    return getField(true, clazz, fieldName);
  }

  private static Field getField(boolean recursion, Class<?> clazz, String fieldName) {
    try {
      return clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      if (recursion && clazz.getSuperclass() != null) {
        return getField(recursion, clazz.getSuperclass(), fieldName);
      }
      return null;
    }
  }
}
