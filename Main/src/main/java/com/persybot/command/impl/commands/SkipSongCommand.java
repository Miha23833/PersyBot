package com.persybot.command.impl.commands;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractCommand;
import com.persybot.command.CommandContext;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;
import java.util.Objects;

public class SkipSongCommand extends AbstractCommand {
    public SkipSongCommand() {
        super(0);
    }

    @Override
    public void execute(CommandContext context) {
        Long channelId = context.getEvent().getGuild().getIdLong();
        GuildVoiceState voiceState = Objects.requireNonNull(context.getEvent().getMember()).getVoiceState();
        AudioManager audioManager = context.getEvent().getGuild().getAudioManager();

        if (!isExecutorAndBotAreInSameVoiceChannel(voiceState, audioManager)) {
            context.getEvent().getChannel().sendMessage("You must be in the same channel as me to skip song.").queue();
        }

        ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(channelId).getAudioPlayer().skip();
    }

    @Override
    public String describe(CommandContext context) {
        return TEXT_COMMAND.SKIP.describeText();
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }
}
