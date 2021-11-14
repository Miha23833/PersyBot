package com.persybot.message.service;

import com.persybot.service.Service;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public interface MessageControlService extends Service {
    void deleteMessage(Message message);

    void deleteMessage(Long channelId, Long messageId);

    void deleteMessages(Long channelId, List<Long> messageIds);
}
