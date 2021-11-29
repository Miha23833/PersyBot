package com.persybot.command.impl.commands;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.utils.BotUtils;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.List;

public class JoinToVoiceChannelCommand extends AbstractTextCommand {

    public JoinToVoiceChannelCommand() {
        super(0);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }

    @Override
    protected boolean runBefore(TextCommandContext context) {
        if (!BotUtils.isMemberInVoiceChannel(context.getEvent().getMember())) {
            BotUtils.sendMessage(new DefaultTextMessage("You are not in voice channel").template(), context.getEvent().getChannel());
            return false;
        }

        if (!BotUtils.canJoin(context.getGuild().getSelfMember(), context.getEvent().getMember().getVoiceState().getChannel())) {
            BotUtils.sendMessage(new DefaultTextMessage("I cannot connect to your channel").template(), context.getEvent().getChannel());
            return false;
        }
        return true;
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        VoiceChannel voiceChannel = context.getEvent().getMember().getVoiceState().getChannel();

        ServiceAggregatorImpl.getInstance().getService(ChannelService.class)
                .getChannel(context.getGuildId())
                .voiceChannelAction().joinChannel(voiceChannel);

        BotUtils.sendMessage(new DefaultTextMessage("Connected to " + voiceChannel.getName()).template(), context.getEvent().getChannel());
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}
