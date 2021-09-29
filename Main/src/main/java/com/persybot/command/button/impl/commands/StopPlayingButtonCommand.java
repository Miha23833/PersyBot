package com.persybot.command.button.impl.commands;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.service.impl.ServiceAggregatorImpl;

public class StopPlayingButtonCommand implements ButtonCommand {
    @Override
    public void execute(ButtonCommandContext context) {
        ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(context.getGuildId()).playerAction().stopMusic();
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }
}
