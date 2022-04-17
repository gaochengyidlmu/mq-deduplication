package com.gcy.mq.deduplication.tools.spring;

import org.springframework.context.ApplicationContext;

public class BeanUtils {
  private static ApplicationContext applicationContext;

  public static void setApplicationContext(ApplicationContext context) {
    applicationContext = context;
  }
}
