package com.persybot.cache.service;

import com.persybot.cache.Cache;
import com.persybot.service.Aggregator;

import java.util.HashMap;
import java.util.Map;

public class CacheServiceImpl implements CacheService {

    Map<Class<? extends Cache>, Cache> services = new HashMap<>();

    @Override
    public <T extends Cache> T get(Class<T> serviceClass) {
        Cache service = services.get(serviceClass);
        if (service != null) {
            return serviceClass.cast(service);
        }
        return null;
    }

    @Override
    public Aggregator<Cache> add(Class<? extends Cache> type, Cache service) {
        services.put(type, service);
        return this;
    }

}
