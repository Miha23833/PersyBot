package com.persybot.message.template.impl;

import com.persybot.message.template.BotColor;
import com.persybot.message.template.MessageTemplate;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class InfoMessage implements MessageTemplate {
    private final String title;
    private final String content;

    public InfoMessage(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public Message template() {
        MessageEmbed embedMessage = new EmbedBuilder().setColor(BotColor.EMBED.color()).setTitle(title).setDescription(content) .build();
        return new MessageBuilder().setEmbeds(embedMessage).build();
    }
}
