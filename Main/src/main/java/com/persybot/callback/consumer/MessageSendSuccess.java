package com.persybot.callback.consumer;

import com.persybot.message.service.MessageAggregatorService;
import com.persybot.message.service.MessageControlService;
import com.persybot.message.service.MessageType;
import com.persybot.service.impl.ServiceAggregatorImpl;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.function.Consumer;

public class MessageSendSuccess<T extends Message> implements Consumer<T> {
    private static MessageAggregatorService messageAggregatorService;
    private static MessageControlService messageControlService;

    private final MessageType type;
    private final Message message;

    public MessageSendSuccess(MessageType type, Message message) {
        if (messageAggregatorService == null) {
            messageAggregatorService = ServiceAggregatorImpl.getInstance().getService(MessageAggregatorService.class);
        }
        if (messageControlService == null) {
            messageControlService = ServiceAggregatorImpl.getInstance().getService(MessageControlService.class);
        }

        this.type = type;
        this.message = message;
    }


    @Override
    public void accept(T t) {
        long textChannelId = message.getTextChannel().getIdLong();

        List<Long> oldMessages = messageAggregatorService.removeOld(type, textChannelId);
        messageControlService.deleteMessages(textChannelId, oldMessages);
        messageAggregatorService.addMessage(type, message.getTextChannel().getIdLong(), message.getIdLong());
    }
}
