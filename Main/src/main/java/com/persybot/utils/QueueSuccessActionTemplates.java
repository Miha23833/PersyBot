package com.persybot.utils;

import com.persybot.message.service.MessageType;
import com.persybot.message.service.SelfFloodController;
import com.persybot.service.impl.ServiceAggregator;
import net.dv8tion.jda.api.entities.Message;

import java.util.function.Consumer;

public interface QueueSuccessActionTemplates {
    static Consumer<Message> addToSelfCleaner(MessageType msgType) {
        return message -> ServiceAggregator.getInstance().get(SelfFloodController.class)
                .addMessage(msgType, message.getTextChannel().getIdLong(), message.getIdLong());
    }
}
