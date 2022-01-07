package com.persybot.message.template.impl;

import com.persybot.message.PAGINATION_BUTTON;
import com.persybot.message.template.MessageTemplate;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.Arrays;

public class PagingMessage implements MessageTemplate {
    private final Message message;
    boolean hasPrev, hasNext;

    public PagingMessage(Message message, boolean hasPrev, boolean hasNext) {
        this.message = message;
        this.hasPrev = hasPrev;
        this.hasNext = hasNext;
    }

    @Override
    public Message template() {
        return new MessageBuilder(message).setActionRows(ActionRow.of(Arrays.asList(
                PAGINATION_BUTTON.PREVIOUS.button(!hasPrev),
                PAGINATION_BUTTON.NEXT.button(!hasNext))
        )).build();
    }
}
