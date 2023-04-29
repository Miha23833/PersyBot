package com.persybot.command;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonCommandContext extends CommandContext<ButtonInteractionEvent> {
    String getButtonId();
}
