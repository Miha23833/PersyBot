package com.persybot.command.impl.commands;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
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
    public void execute(TextCommandContext context) {
        ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(context.getGuildId()).playerAction().mixQueue();

        context.getEvent().getChannel().sendMessage(new DefaultTextMessage("Playing queue was mixed.").template()).queue();
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}
