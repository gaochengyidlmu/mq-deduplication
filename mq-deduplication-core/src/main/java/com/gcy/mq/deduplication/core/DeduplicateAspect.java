package com.gcy.mq.deduplication.core;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DeduplicateAspect {
  @Autowired public RedissonClient redissonClient;

  @Value("${spring.application.name}")
  public String applicationName;

  @Around("@annotation(mQDeduplication)")
  public void deduplication(ProceedingJoinPoint joinPoint, MQDeduplication mQDeduplication)
      throws Throwable {
    Object[] args = joinPoint.getArgs();
    MessageExt msgExt = (MessageExt) args[0];

    String lockKey = applicationName + ":" + msgExt.getTopic() + ":" + msgExt.getMsgId();
    RLock lock = redissonClient.getLock(lockKey);

    // 获取锁成功时，消费该消息
    // 获取锁失败，代表消息被其它消费者在消费，直接报错，让消息重发
    if (lock.tryLock()) {
      log.info("acquire lock success: " + lockKey);
      try {
        String stateKey = lockKey + ":state";
        RBucket<Integer> state = redissonClient.getBucket(stateKey);

        // 消息没有执行过，此时执行业务代码
        if (state.get() == null) {
          state.set(HandleState.PROCESSING.getValue(), 1, TimeUnit.DAYS);
          executeProceed(joinPoint, args, lock, state);
        }
        // 消息已处理成功，直接返回
        else if (Objects.equals(HandleState.valueOf(state.get()), HandleState.SUCCESS)) {
          log.info(
              "duplicate message which has been processed success, skip this message. "
                  + msgExt.getMsgId());
        }
        // 消息处理中，但是获取到了 lock，说明之前的业务处理有问题，此时重新执行
        else if (Objects.equals(HandleState.valueOf(state.get()), HandleState.PROCESSING)) {
          log.info(
              "duplicate message which is processing, reconsume this message. "
                  + msgExt.getMsgId());
          executeProceed(joinPoint, args, lock, state);
        }
        // 消息处理失败，重新执行
        else if (Objects.equals(HandleState.valueOf(state.get()), HandleState.FAILED)) {
          log.info(
              "duplicate message which is failed, reconsume this message. " + msgExt.getMsgId());
          executeProceed(joinPoint, args, lock, state);
        }
      } finally {
        lock.unlock();
        log.info("release lock success: " + lockKey);
      }
    } else {
      log.info("acquire lock failed: " + lockKey);
      throw new RuntimeException("Retry consume message");
    }
  }

  private void executeProceed(
      ProceedingJoinPoint joinPoint, Object[] args, RLock lock, RBucket<Integer> state)
      throws Throwable {
    try {
      joinPoint.proceed(args);
    } catch (Exception e) {
      state.set(HandleState.FAILED.getValue(), 1, TimeUnit.DAYS);
      throw e;
    }
    state.set(HandleState.SUCCESS.getValue(), 1, TimeUnit.DAYS);
  }
}
