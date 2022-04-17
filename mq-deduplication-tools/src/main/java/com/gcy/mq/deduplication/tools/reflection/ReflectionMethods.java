package com.gcy.mq.deduplication.tools.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionMethods {

  public static Method[] getMethods(Class<?> clazz) {
    return clazz.getDeclaredMethods();
  }

  // 查找指定方法，只从本类中查找
  public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
    return getMethod(false, clazz, methodName, parameterTypes);
  }

  // 根据字段，获取 getter 方法
  public static Method getGetter(Class<?> clazz, Field field) {
    return ReflectionMethods.getMethod(
        clazz, ReflectionMethods.buildGetMethodName(field.getName()));
  }

  // 根据字段，获取 setter 方法
  public static Method setGetter(Class<?> clazz, Field field) {
    return ReflectionMethods.findMethodByName(
        clazz, ReflectionMethods.buildSetMethodName(field.getName()));
  }

  // 查找指定方法，包含父类的方法
  public static Method getMethodIncludeParent(
      Class<?> clazz, String methodName, Class<?>... parameterTypes) {
    return getMethod(true, clazz, methodName, parameterTypes);
  }

  // 获取指定的方法
  private static Method getMethod(
      boolean recursion, Class<?> clazz, String methodName, Class<?>... parameterTypes) {
    Method declaredMethod = null;
    try {
      declaredMethod = clazz.getDeclaredMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      if (recursion && clazz.getSuperclass() != null) {
        return getMethod(recursion, clazz.getSuperclass(), methodName, parameterTypes);
      }
    }
    return declaredMethod;
  }

  public static Method findMethodByName(Class<?> clazz, String methodName) {
    Method[] methods = getMethods(clazz);
    for (Method method : methods) {
      if (method.getName().equals(methodName)) {
        return method;
      }
    }
    return null;
  }

  public static Object invoke(Method method, Object target, Object... args) {
    try {
      return method.invoke(target, args);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  // 比较两个方法是否相同，需要名字、参数都相同
  public static boolean compareMethod(Method m1, Method m2) {
    if (m1 == null || m2 == null) {
      return false;
    }
    if (!m1.getName().equals(m2.getName())) {
      return false;
    }
    if (m1.getParameterCount() != m2.getParameterCount()) {
      return false;
    }
    for (int i = 0; i < m1.getParameterCount(); i++) {
      if (!m1.getParameters()[i].equals(m2.getParameters()[i])) {
        return false;
      }
    }
    return true;
  }

  public static String buildGetMethodName(String fieldName) {
    return buildMethodName(fieldName, "get");
  }

  public static String buildSetMethodName(String fieldName) {
    return buildMethodName(fieldName, "set");
  }

  private static String buildMethodName(String fieldName, String prefix) {
    return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }
}
