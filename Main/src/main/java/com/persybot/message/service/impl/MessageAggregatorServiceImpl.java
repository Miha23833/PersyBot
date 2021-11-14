package com.persybot.message.service.impl;

import com.persybot.collections.list.FreshLimitedQueue;
import com.persybot.collections.list.impl.FreshLimitedQueueImpl;
import com.persybot.message.service.MessageAggregatorService;
import com.persybot.message.service.MessageType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageAggregatorServiceImpl implements MessageAggregatorService {
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static volatile MessageAggregatorServiceImpl INSTANCE;

    private final Map<Long, Map<MessageType, FreshLimitedQueue<Long>>> messages;

    protected MessageAggregatorServiceImpl() {messages = new HashMap<>();}

    public static MessageAggregatorServiceImpl getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new MessageAggregatorServiceImpl();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    @Override
    public void addMessage(MessageType messageType, Long guildId, Long msgId) {
        getQueue(messageType, guildId).add(msgId);
    }

    @Override
    public void deleteOld(MessageType messageType, Long guildId) {
        getQueue(messageType, guildId).clearOld();
    }

    @Override
    public List<Long> removeOld(MessageType messageType, Long guildId) {
        return getQueue(messageType, guildId).clearOld();
    }

    @Override
    public void deleteAll(MessageType messageType, Long guildId) {
        this.messages
                .computeIfAbsent(guildId, x -> new HashMap<>())
                .computeIfAbsent(messageType, x -> new FreshLimitedQueueImpl<>(messageType.getMaxMessagesCount()))
                .clear();
    }

    private FreshLimitedQueue<Long> getQueue(MessageType messageType, Long guildId) {
        return getMapByMessageType(guildId)
                .computeIfAbsent(messageType, x -> new FreshLimitedQueueImpl<>(messageType.getMaxMessagesCount()));
    }

    private Map<MessageType, FreshLimitedQueue<Long>> getMapByMessageType(Long guildId) {
        return this.messages.computeIfAbsent(guildId, x -> new HashMap<>());
    }
}
