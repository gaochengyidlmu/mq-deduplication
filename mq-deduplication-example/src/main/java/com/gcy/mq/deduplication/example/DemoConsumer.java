package com.gcy.mq.deduplication.example;

import com.gcy.mq.deduplication.core.MQDeduplication;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "TopicTest", consumerGroup = "DeduplicationGroup")
public class DemoConsumer implements RocketMQListener<MessageExt> {
  @MQDeduplication
  public void onMessage(MessageExt msg) {
    System.out.println(msg);
  }
}
