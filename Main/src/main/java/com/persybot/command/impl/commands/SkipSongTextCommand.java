package com.persybot.command.impl.commands;

import com.persybot.callback.consumer.MessageSendSuccess;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.service.MessageType;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;
import java.util.Objects;

public class SkipSongTextCommand extends AbstractTextCommand {
    public SkipSongTextCommand() {
        super(0);
    }

    @Override
    public void execute(TextCommandContext context) {
        int skipCount = 1;
        if (context.getArgs().size() >= 1) {
            try {
                skipCount = Integer.parseInt(context.getArgs().get(0));

                if (skipCount < 1) {
                    sendIncorrectCountOfSkipsMessage(context);
                    return;
                }
            }
            catch (NumberFormatException e) {
                sendIncorrectCountOfSkipsMessage(context);
                return;
            }
        }

        Long channelId = context.getEvent().getGuild().getIdLong();
        GuildVoiceState voiceState = Objects.requireNonNull(context.getEvent().getMember()).getVoiceState();
        AudioManager audioManager = context.getEvent().getGuild().getAudioManager();

        if (!isExecutorAndBotAreInSameVoiceChannel(voiceState, audioManager)) {
            context.getEvent().getChannel().sendMessage(new InfoMessage(null, "You must be in the same channel as me to skip song.").template()).queue(x -> new MessageSendSuccess<>(MessageType.BUTTON_ERROR, x).accept(x));
            return;
        }

        if (skipCount == 1) {
            ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(channelId)
                    .playerAction().skipSong();
        } else {
            ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(channelId)
                    .playerAction().skipSong(skipCount);
        }
    }

    @Override
    public String describe(TextCommandContext context) {
        return TEXT_COMMAND.SKIP.describeText();
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }

    private void sendIncorrectCountOfSkipsMessage(TextCommandContext context) {
        context.getEvent().getChannel().sendMessage(new DefaultTextMessage("Incorrect count of skipping songs.").template()).queue();
    }
}
