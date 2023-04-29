package com.persybot.command.impl.commands;

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

public class LeaveChannelTextCommand extends AbstractTextCommand {
    public LeaveChannelTextCommand() {
        super(0);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }

    @Override
    protected boolean runBefore(TextCommandContext context) {
        if (context.getEvent().getMember() == null) return false;

        if (!BotUtils.isMemberInVoiceChannel(context.getGuild().getSelfMember())) {
            BotUtils.sendMessage("I am not connected to a voice channel", context.getEvent().getChannel().asTextChannel());
            return false;
        }

        if (!BotUtils.isMemberInSameVoiceChannelAsBot(context.getGuild().getSelfMember(), context.getEvent().getMember())){
            BotUtils.sendMessage("You must be in the same channel as me", context.getEvent().getChannel().asTextChannel());
            return false;
        }
        return true;
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        ServiceAggregator.getInstance().get(ChannelService.class)
                .getChannel(context.getGuildId())
                .voiceChannelAction().leaveChannel();
        return true;
    }

    @Override
    protected boolean runAfter(TextCommandContext context) {

        BotUtils.sendMessage(new DefaultTextMessage("Left voice channel").template(), context.getEvent().getChannel().asTextChannel());
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}
