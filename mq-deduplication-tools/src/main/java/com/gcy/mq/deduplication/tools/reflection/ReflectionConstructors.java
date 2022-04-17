package com.gcy.mq.deduplication.tools.reflection;

import java.lang.reflect.Constructor;

public class ReflectionConstructors {

  public static <T> T newInstance(Class<T> clazz) {
    try {
      Constructor<T> constructor = clazz.getConstructor();
      return constructor.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
