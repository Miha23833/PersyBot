package com.persybot.service;

public interface Aggregator<S> {
    <T extends S> T get(Class<T> serviceClass);

    Aggregator<S> add(Class<? extends S> type, S value);
}
