package com.persybot.message.template.impl;

import com.persybot.message.template.MessageTemplate;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

public class DefaultTextMessage implements MessageTemplate {
    private final String message;

    public DefaultTextMessage(String message) {this.message = message;}

    @Override
    public Message template() {
        return new MessageBuilder().setContent(toBold(message)).build();
    }

    private String toBold(String text) {
        return String.join("", "**", text, "**");
    }
}
