package com.persybot.command.button.impl.commands;

import com.persybot.callback.consumer.MessageSendSuccess;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.service.MessageType;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;

public class SkipSongButtonCommand implements ButtonCommand {
    @Override
    public void execute(ButtonCommandContext context) {
        GuildVoiceState voiceState = Objects.requireNonNull(context.getEvent().getMember()).getVoiceState();
        AudioManager audioManager = context.getEvent().getGuild().getAudioManager();

        if (!isExecutorAndBotAreInSameVoiceChannel(voiceState, audioManager)) {
            context.getEvent().getChannel().sendMessage(new InfoMessage(null, "You must be in the same channel as me to skip song.").template()).queue(x -> new MessageSendSuccess<>(MessageType.BUTTON_ERROR, x).accept(x));
            return;
        }

        ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(context.getGuildId()).playerAction().skipSong();
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }

    private boolean isExecutorInVoiceChannel(GuildVoiceState memberVoiceState) {
        return memberVoiceState != null && memberVoiceState.inVoiceChannel() && memberVoiceState.getChannel() != null;
    }

    private boolean isExecutorAndBotAreInSameVoiceChannel(GuildVoiceState memberVoiceState, AudioManager audioManager) {
        return (isExecutorInVoiceChannel(memberVoiceState) && audioManager.isConnected());
    }
}
