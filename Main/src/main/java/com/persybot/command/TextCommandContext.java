package com.persybot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface TextCommandContext extends CommandContext<MessageReceivedEvent> {
    List<String> getArgs();

    String getCommand();
}
