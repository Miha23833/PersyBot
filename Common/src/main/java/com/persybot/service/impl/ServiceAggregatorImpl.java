package com.persybot.service.impl;

import com.persybot.service.Service;
import com.persybot.service.ServiceAggregator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServiceAggregatorImpl implements ServiceAggregator {
    private static ServiceAggregatorImpl INSTANCE;
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public static ServiceAggregatorImpl getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new ServiceAggregatorImpl();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    private ServiceAggregatorImpl(){}

    Map<Class<? extends Service>, Service> services = new HashMap<>();

    @Override
    public <T extends Service> T getService(Class<T> serviceClass) {
        Service service = services.get(serviceClass);
        if (service != null) {
            return serviceClass.cast(service);
        }
        return null;
    }

    @Override
    public ServiceAggregator addService(Class<? extends Service> serviceClass, Service service) {
        services.put(serviceClass, service);
        return this;
    }
}
