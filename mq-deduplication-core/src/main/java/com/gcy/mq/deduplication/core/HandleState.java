package com.gcy.mq.deduplication.core;

import lombok.Getter;

public enum HandleState {
  PROCESSING(1),
  SUCCESS(2),
  FAILED(3),
  ;

  @Getter private final int value;

  HandleState(int value) {
    this.value = value;
  }

  public static HandleState valueOf(int value) {
    for (HandleState state : HandleState.values()) {
      if (state.value == value) return state;
    }
    return null;
  }
}
