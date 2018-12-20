package com.bigpanda.commons.streams;

public interface ThrottledStream<T> {
    void incrementLeftQuota();
    void breakQuota();
}
