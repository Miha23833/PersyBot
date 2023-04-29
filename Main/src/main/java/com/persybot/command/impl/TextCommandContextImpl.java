package com.persybot.command.impl;

import com.persybot.command.TextCommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class TextCommandContextImpl implements TextCommandContext {
    private final MessageReceivedEvent event;
    private final List<String> args;
    private final String command;

    public TextCommandContextImpl(MessageReceivedEvent event, String prefix) {
        this.event = Objects.requireNonNull(event);
        Objects.requireNonNull(prefix);

        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(prefix), "")
                .split("\\s+");

        this.command = split[0];


        this.args = Arrays.asList(split).subList(1, split.length);
    }

    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @Override
    public MessageReceivedEvent getEvent() {
        return event;
    }

    @Override
    public List<String> getArgs() {
        return args;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public Long getGuildId() {
        return getGuild().getIdLong();
    }
}
