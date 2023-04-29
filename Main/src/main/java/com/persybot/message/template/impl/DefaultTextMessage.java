package com.persybot.message.template.impl;

import com.persybot.message.template.MessageTemplate;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class DefaultTextMessage implements MessageTemplate {
    private final String message;

    public DefaultTextMessage(String message) {this.message = message;}

    @Override
    public MessageCreateData template() {
        return new MessageCreateBuilder().setContent(toBold(message)).build();
    }

    private String toBold(String text) {
        return String.join("", "**", text, "**");
    }
}
