package com.persybot.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;

public interface CommandContext <E extends Event> {
    Guild getGuild();

    E getEvent();

    Long getGuildId();
}
