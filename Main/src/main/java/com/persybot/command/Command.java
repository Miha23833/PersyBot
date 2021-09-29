package com.persybot.command;

import net.dv8tion.jda.api.events.Event;

public interface Command<C extends CommandContext<Event>> {
    void execute(C context);

    String describe(C context);
}
