package com.persybot.command.button.impl;

import com.persybot.command.ButtonCommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Objects;

public class ButtonCommandContextImpl implements ButtonCommandContext {
    private final ButtonInteractionEvent event;

    public ButtonCommandContextImpl(ButtonInteractionEvent event) {
        this.event = event;
    }

    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @Override
    public ButtonInteractionEvent getEvent() {
        return event;
    }

    @Override
    public Long getGuildId() {
        return getGuild().getIdLong();
    }

    @Override
    public String getButtonId() {
        return Objects.requireNonNull(event.getButton()).getId();
    }
}
