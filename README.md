# MQ DEDUPLICATION
#### 功能描述
无侵入性的 mq 去重模块

#### 使用示例
与 RocketMQ starter 结合，通过注解，能够在消费消息时，进行消息去重。
```java
@Component
@RocketMQMessageListener(topic = "TopicTest", consumerGroup = "DeduplicationGroup")
public class DemoConsumer implements RocketMQListener<MessageExt> {
  @MQDeduplication
  public void onMessage(MessageExt msg) {
    if (msg != null) {
      throw new RuntimeException("123");
    }
    System.out.println(msg);
  }
}
```