package com.persybot.command.impl.commands;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;

public class StopPlayingTextCommand extends AbstractTextCommand {
    public StopPlayingTextCommand() {
        super(0);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        ServiceAggregator.getInstance().get(ChannelService.class).getChannel(context.getGuildId())
                .playerAction().stopMusic();
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}
