package com.persybot.message.service;

import com.persybot.service.Service;

import java.util.List;

public interface MessageAggregatorService extends Service {
    void addMessage(MessageType messageType, Long guildId, Long msgId);

    void deleteOld(MessageType messageType, Long guildId);

    List<Long> removeOld(MessageType messageType, Long guildId);

    void deleteAll(MessageType messageType, Long guildId);
}
