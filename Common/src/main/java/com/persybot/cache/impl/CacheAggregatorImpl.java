package com.persybot.cache.impl;

import com.persybot.cache.Cache;
import com.persybot.cache.CacheAggregator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheAggregatorImpl implements CacheAggregator {
    private static CacheAggregatorImpl INSTANCE;

    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final ConcurrentHashMap<Class<?>, Cache<?, ?>> cacheContainer;

    private CacheAggregatorImpl() {
        cacheContainer = new ConcurrentHashMap<>();
    }

    public static CacheAggregatorImpl getINSTANCE() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new CacheAggregatorImpl();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }


    @Override
    public Cache<?, ?> getCache(Class<?> identifier) {
        return cacheContainer.get(identifier);
    }

    @Override
    public CacheAggregator addCache(Class<?> identifier, Cache<?, ?> cache) {
        cacheContainer.put(identifier, cache);
        return this;
    }

    @Override
    public CacheAggregator removeCache(Class<?> identifier) {
        cacheContainer.remove(identifier);
        return this;
    }
}
