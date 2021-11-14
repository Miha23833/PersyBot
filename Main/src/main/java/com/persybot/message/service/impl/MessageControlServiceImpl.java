package com.persybot.message.service.impl;

import com.persybot.message.service.MessageControlService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageControlServiceImpl implements MessageControlService {
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static volatile MessageControlServiceImpl INSTANCE;

    private final JDA jda;

    protected MessageControlServiceImpl(JDA jda) {this.jda = jda;}

    public static MessageControlServiceImpl getInstance(JDA jda) {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new MessageControlServiceImpl(jda);
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    @Override
    public void deleteMessage(Message message) {
        deleteMessage(message.getChannel().getIdLong(), message.getIdLong());
    }

    @Override
    public void deleteMessage(Long channelId, Long messageId) {
        new AuditableRestActionImpl<>(jda, Route.Messages.DELETE_MESSAGE.compile(String.valueOf(channelId), String.valueOf(messageId))).queue();
    }

    @Override
    public void deleteMessages(Long channelId, List<Long> messageIds) {
        if (messageIds.size() == 1) {
            deleteMessage(channelId, messageIds.get(0));
        }
        else if (messageIds.size() > 1) {
            DataObject body = DataObject.empty().put("messages", messageIds);
            Route.CompiledRoute route = Route.Messages.DELETE_MESSAGES.compile(String.valueOf(channelId));
            new RestActionImpl<>(jda, route, body).queue();
        }
    }
}
