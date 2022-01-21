package com.persybot.message.service;

import com.persybot.service.Service;

public interface SelfFloodController extends Service {
    void addMessage(MessageType messageType, Long textChannelId, Long msgId);
}
