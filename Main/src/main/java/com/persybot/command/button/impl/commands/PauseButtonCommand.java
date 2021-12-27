package com.persybot.command.button.impl.commands;

import com.persybot.callback.consumer.MessageSendSuccess;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.PLAYER_BUTTON;
import com.persybot.message.service.MessageType;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;

public class PauseButtonCommand implements ButtonCommand {
    @Override
    public void execute(ButtonCommandContext context) {
        ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(context.getGuildId()).playerAction().pauseSong();
        context.getEvent().getInteraction().editButton(PLAYER_BUTTON.RESUME.button(false)).queue();

        context.getEvent().getChannel().sendMessage(new DefaultTextMessage("Player paused").template()).queue(x -> new MessageSendSuccess<>(MessageType.PLAYER_STATE, x).accept(x));


    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }
}
