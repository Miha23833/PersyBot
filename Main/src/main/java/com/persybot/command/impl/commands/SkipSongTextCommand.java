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

public class SkipSongTextCommand extends AbstractTextCommand {
    public SkipSongTextCommand() {
        super(0);
    }

    @Override
    protected boolean runBefore(TextCommandContext context) {
        int skipCount;
        if (context.getArgs().size() >= 1) {
            try {
                skipCount = Integer.parseInt(context.getArgs().get(0));

                if (skipCount < 1) {
                    sendIncorrectCountOfSkipsMessage(context);
                    return false;
                }
            }
            catch (NumberFormatException e) {
                sendIncorrectCountOfSkipsMessage(context);
                return false;
            }
        }
        if (context.getEvent().getMember() == null) return false;

        if (!BotUtils.isMemberInSameVoiceChannelAsBot(context.getEvent().getMember(), context.getGuild().getSelfMember())) {
            BotUtils.sendMessage(new DefaultTextMessage("You must be in the same channel as me to skip song").template(), context.getEvent().getChannel());
            return false;
        }
        return true;
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        int skipCount = 1;
        if (context.getArgs().size() >= 1) {
            skipCount = Integer.parseInt(context.getArgs().get(0));
        }
        Long channelId = context.getEvent().getGuild().getIdLong();

        Channel channel = ServiceAggregator.getInstance().get(ChannelService.class).getChannel(channelId);
        if (channel.hasInitiatedAudioPlayer()) {
            channel.playerAction().skipSong(skipCount);
        }
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return "Use it to skip current song or few of them if you pass some number as argument";
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }

    private void sendIncorrectCountOfSkipsMessage(TextCommandContext context) {
        BotUtils.sendMessage(new DefaultTextMessage("Incorrect count of skipping songs").template(),  context.getEvent().getChannel());
    }
}
