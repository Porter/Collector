package com.porter.collector.util;

public interface Provider<T, A> {
    T get(A arg);
}
