package com.persybot.command.button.impl.commands;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.PLAYER_BUTTON;
import com.persybot.message.service.MessageType;
import com.persybot.message.service.SelfFloodController;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregator;

public class PauseButtonCommand implements ButtonCommand {
    public PauseButtonCommand() {
    }

    @Override
    public void execute(ButtonCommandContext context) {
        ServiceAggregator.getInstance().get(ChannelService.class).getChannel(context.getGuildId()).playerAction().pauseSong();
        context.getEvent().getInteraction().editButton(PLAYER_BUTTON.RESUME.button(false)).queue();

        context.getEvent().getChannel().sendMessage(new DefaultTextMessage("Player paused").template())
                .queue(x -> ServiceAggregator.getInstance().get(SelfFloodController.class)
                        .addMessage(MessageType.PLAYER_STATE, x.getTextChannel().getIdLong(), x.getIdLong()));
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }
}
