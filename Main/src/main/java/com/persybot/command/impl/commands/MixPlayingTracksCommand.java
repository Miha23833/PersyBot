package com.persybot.command.impl.commands;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.utils.BotUtils;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;

public class MixPlayingTracksCommand extends AbstractTextCommand {
    public MixPlayingTracksCommand() {
        super(0);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        Channel channel = ServiceAggregator.getInstance().get(ChannelService.class).getChannel(context.getGuildId());
        if (!channel.hasInitiatedAudioPlayer()) {
            return false;
        }
        channel.playerAction().mixQueue();
        return true;
    }

    @Override
    protected boolean runAfter(TextCommandContext context) {
        // TODO show new queue
        BotUtils.sendMessage(new DefaultTextMessage("Playing queue was mixed").template(), context.getEvent().getChannel().asTextChannel());
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}
