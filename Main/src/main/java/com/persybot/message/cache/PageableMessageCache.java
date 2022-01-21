package com.persybot.message.cache;

import com.persybot.cache.Cache;
import com.persybot.message.PAGEABLE_MESSAGE_TYPE;
import com.persybot.paginator.PageableMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class PageableMessageCache implements Cache {
    private final ReadWriteLock readWriteLock;
    private final Map<Long, Map<PAGEABLE_MESSAGE_TYPE, PageableMessage>> cache;

    public PageableMessageCache() {
        this.cache = new HashMap<>();
        readWriteLock = new ReentrantReadWriteLock();
    }

    public void add(Long textChannelId, PAGEABLE_MESSAGE_TYPE type, PageableMessage msg) {
        assert textChannelId != null;
        assert msg.getMessageId() != null;

        try {
            readWriteLock.writeLock().lock();
            this.cache.computeIfAbsent(textChannelId, (k) -> new HashMap<>()).put(type, msg);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void remove(Long textChannelId, Long messageId) {
        try {
            readWriteLock.writeLock().lock();
            AtomicReference<PAGEABLE_MESSAGE_TYPE> key = new AtomicReference<>();
            this.cache.computeIfAbsent(textChannelId, (x) -> new HashMap<>())
                    .entrySet()
                    .stream()
                    .filter( es -> messageId.equals(es.getValue().getMessageId()))
                    .findFirst().ifPresent( x -> key.set(x.getKey()));
            if (key.get() != null) {
                this.cache.get(textChannelId).remove(key.get());
            }

        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public PageableMessage get(Long textChannelId, Long messageId) {
        try {
            readWriteLock.readLock().lock();
            if (this.cache.containsKey(textChannelId)) {
                return cache.get(textChannelId).values().stream().filter( x -> x.getMessageId().equals(messageId)).findFirst().orElse(null);
            }
            return null;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public boolean contains(Long textChannelId, Long messageId) {
        return get(textChannelId, messageId) != null;
    }
}
