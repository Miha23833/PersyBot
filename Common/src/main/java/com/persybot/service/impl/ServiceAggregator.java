package com.persybot.service.impl;

import com.persybot.service.Aggregator;
import com.persybot.service.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServiceAggregator implements Aggregator<Service> {
    private static ServiceAggregator INSTANCE;
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public static ServiceAggregator getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new ServiceAggregator();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    private ServiceAggregator(){}

    Map<Class<? extends Service>, Service> services = new HashMap<>();

    @Override
    public <T extends Service> T get(Class<T> serviceClass) {
        Service service = services.get(serviceClass);
        if (service != null) {
            return serviceClass.cast(service);
        }
        return null;
    }

    @Override
    public Aggregator<Service> add(Class<? extends Service> type, Service value) {
        services.put(type, value);
        return this;
    }
}
