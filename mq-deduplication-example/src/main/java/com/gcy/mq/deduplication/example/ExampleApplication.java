package com.gcy.mq.deduplication.example;

import com.gcy.mq.deduplication.core.EnableMQDeduplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMQDeduplication
@SpringBootApplication
public class ExampleApplication {
  public static void main(String[] args) {
    SpringApplication.run(ExampleApplication.class);
  }
}
