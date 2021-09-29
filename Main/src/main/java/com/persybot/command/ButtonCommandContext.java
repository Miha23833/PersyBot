package com.persybot.command;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ButtonCommandContext extends CommandContext<ButtonClickEvent> {
    String getButtonId();
}
