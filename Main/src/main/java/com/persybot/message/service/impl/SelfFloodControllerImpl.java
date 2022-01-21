package com.persybot.message.service.impl;

import com.persybot.collections.list.FreshLimitedQueue;
import com.persybot.collections.list.impl.FreshLimitedQueueImpl;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.service.MessageType;
import com.persybot.message.service.SelfFloodController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class SelfFloodControllerImpl implements SelfFloodController {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Map<Long, Map<MessageType, FreshLimitedQueue<Long>>> messages;
    private final JDA jda;

    public SelfFloodControllerImpl(JDA jda) {
        this.messages = new HashMap<>();
        this.jda = jda;
    }

    @Override
    public void addMessage(MessageType messageType, Long textChannelId, Long msgId) {
        this.rwLock.writeLock().lock();
        try {
            FreshLimitedQueue<Long> messageQueue = getQueue(textChannelId, messageType);
            messageQueue.add(msgId);
            if (!messageQueue.isOldEmpty()) {
                List<Long> messagesToRemove = messageQueue.clearOld();
                TextChannel channel = this.jda.getTextChannelById(textChannelId);
                if (channel == null) {
                    PersyBotLogger.BOT_LOGGER.info(String.format("Cannot find text channel with ID = %s", textChannelId));
                    this.messages.remove(textChannelId);
                    return;
                }
                if (messagesToRemove.size() == 1) {
                    channel.deleteMessageById(messagesToRemove.get(0)).queue();
                } else if (messagesToRemove.size() > 1) {
                    channel.deleteMessagesByIds(messagesToRemove.stream().map(Object::toString).collect(Collectors.toList())).queue();
                }
            }
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    private FreshLimitedQueue<Long> getQueue(Long guildId, MessageType messageType) {
        return getMapByMessageType(guildId)
                .computeIfAbsent(messageType, x -> new FreshLimitedQueueImpl<>(messageType.getMaxMessagesCount()));
    }

    private Map<MessageType, FreshLimitedQueue<Long>> getMapByMessageType(Long guildId) {
        return this.messages.computeIfAbsent(guildId, x -> new HashMap<>());
    }
}
