package com.persybot.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public interface TextCommandContext extends CommandContext<GuildMessageReceivedEvent> {
    List<String> getArgs();

    String getCommand();
}
