package com.persybot.service;

public interface ServiceAggregator {
    <T extends Service> T getService(Class<T> serviceClass);

    ServiceAggregator addService(Class<? extends Service> serviceClass, Service service);
}
