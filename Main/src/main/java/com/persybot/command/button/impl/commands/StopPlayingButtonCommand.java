package com.persybot.command.button.impl.commands;

import com.persybot.callback.consumer.MessageSendSuccess;
import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.service.MessageType;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;

public class StopPlayingButtonCommand implements ButtonCommand {
    @Override
    public void execute(ButtonCommandContext context) {
        Channel channel = ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(context.getGuildId());
        if (channel.getAudioPlayer().isPlaying()) {
            channel.playerAction().stopMusic();
            context.getEvent().getChannel().sendMessage(new DefaultTextMessage("Player stopped").template()).queue(x -> new MessageSendSuccess<>(MessageType.PLAYER_STATE, x).accept(x));
        }
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }
}
