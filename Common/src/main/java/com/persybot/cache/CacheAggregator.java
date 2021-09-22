package com.persybot.cache;

public interface CacheAggregator {
    Cache getCache(Class<?> identifier);

    CacheAggregator addCache(Class<?> identifier, Cache cache);

    CacheAggregator removeCache(Class<?> identifier);
}
