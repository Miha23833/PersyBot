package com.persybot.staticdata;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StaticDataImpl implements StaticData {
    private static StaticDataImpl INSTANCE;
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final Map<Long, Long> guildsWithActiveVoiceChannel = new HashMap<>();

    public Map<Long, Long> getGuildsWithActiveVoiceChannel() {
        return guildsWithActiveVoiceChannel;
    }

    private StaticDataImpl(){
    }

    public static StaticDataImpl getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new StaticDataImpl();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }
}
