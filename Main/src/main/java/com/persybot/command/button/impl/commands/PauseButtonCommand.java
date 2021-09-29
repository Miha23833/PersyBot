package com.persybot.command.button.impl.commands;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.PLAYER_BUTTON;
import com.persybot.service.impl.ServiceAggregatorImpl;

public class PauseButtonCommand implements ButtonCommand {
    @Override
    public void execute(ButtonCommandContext context) {
        ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(context.getGuildId()).playerAction().pauseSong();
        context.getEvent().getInteraction().editButton(PLAYER_BUTTON.RESUME.button(false)).queue();
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }
}
