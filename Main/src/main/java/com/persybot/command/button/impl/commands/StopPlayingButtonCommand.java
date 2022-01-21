package com.persybot.command.button.impl.commands;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.service.MessageType;
import com.persybot.message.service.SelfFloodController;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregator;

public class StopPlayingButtonCommand implements ButtonCommand {

    public StopPlayingButtonCommand() {
    }

    @Override
    public void execute(ButtonCommandContext context) {
        Channel channel = ServiceAggregator.getInstance().get(ChannelService.class).getChannel(context.getGuildId());
        if (channel.getAudioPlayer().isPlaying()) {
            channel.playerAction().stopMusic();
            context.getEvent().getChannel().sendMessage(new DefaultTextMessage("Player stopped").template())
                    .queue(x -> ServiceAggregator.getInstance().get(SelfFloodController.class)
                            .addMessage(MessageType.PLAYER_STATE, x.getTextChannel().getIdLong(), x.getIdLong()));
        }
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }
}
