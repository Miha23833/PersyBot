package com.persybot.cache;

public interface Cache<K, V> {
    void addObject(V track);

    void removeObject(K identifier);

    V getObject(K identifier);

    boolean exists(K identifier);
    
    void clearCache();

    default void removeExpired() {
        
    }
}
