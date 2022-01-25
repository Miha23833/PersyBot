package com.persybot.command.impl.commands;

import com.persybot.channel.impl.ChannelImpl;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.validation.ValidationResult;

import java.util.List;


//TODO: remove this command after bug confirm
public class ResetAudioTextCommand extends AbstractTextCommand {

    public ResetAudioTextCommand() {
        super(0);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return null;
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        if (context.getEvent().getAuthor().getIdLong() != 235816331138695168L) {
            return false;
        }

        ChannelImpl channel = (ChannelImpl) ServiceAggregator.getInstance().get(ChannelService.class).getChannel(context.getGuildId());
        channel.resetAudioPlayer();

        context.getEvent().getGuild().getAudioManager().setSendingHandler(channel.getAudioPlayer().getSendHandler());

        context.getEvent().getMessage().getTextChannel().sendMessage("Audio player was reset").queue();

        return false;
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}
