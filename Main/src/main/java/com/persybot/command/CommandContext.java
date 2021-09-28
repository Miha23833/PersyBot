package com.persybot.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public interface CommandContext {
    Guild getGuild();

    GuildMessageReceivedEvent getEvent();

    List<String> getArgs();

    String getCommand();

    Long getGuildId();
}
