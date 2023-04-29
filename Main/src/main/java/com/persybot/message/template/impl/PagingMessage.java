package com.persybot.message.template.impl;

import com.persybot.message.PAGINATION_BUTTON;
import com.persybot.message.template.MessageTemplate;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class PagingMessage implements MessageTemplate {
    private final MessageCreateData message;
    boolean hasPrev, hasNext;

    public PagingMessage(MessageCreateData message, boolean hasPrev, boolean hasNext) {
        this.message = message;
        this.hasPrev = hasPrev;
        this.hasNext = hasNext;
    }

    @Override
    public MessageCreateData template() {
        return new MessageCreateBuilder().setActionRow(
                PAGINATION_BUTTON.PREVIOUS.button(!hasPrev),
                PAGINATION_BUTTON.NEXT.button(!hasNext)).build();
    }
}
