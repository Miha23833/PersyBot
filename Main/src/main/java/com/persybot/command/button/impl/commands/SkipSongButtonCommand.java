package com.persybot.command.button.impl.commands;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.ButtonCommand;
import com.persybot.command.ButtonCommandContext;
import com.persybot.message.service.MessageType;
import com.persybot.message.service.SelfFloodController;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.service.impl.ServiceAggregator;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.managers.AudioManager;

public class SkipSongButtonCommand implements ButtonCommand {

    public SkipSongButtonCommand() {
    }


    @Override
    public void execute(ButtonCommandContext context) {
        AudioManager audioManager = context.getEvent().getGuild().getAudioManager();

        if (audioManager.getSendingHandler() == null) {
            return;
        }
        GuildVoiceState voiceState = context.getEvent().getMember().getVoiceState();

        if (!isExecutorAndBotAreInSameVoiceChannel(voiceState, audioManager)) {
            context.getEvent().getChannel().sendMessage(new InfoMessage(null, "You must be in the same channel as me to skip song").template())
                    .queue(x -> ServiceAggregator.getInstance().get(SelfFloodController.class)
                            .addMessage(MessageType.BUTTON_ERROR, x.getChannel().asTextChannel().getIdLong(), x.getIdLong()));
            return;
        }

        ServiceAggregator.getInstance().get(ChannelService.class).getChannel(context.getGuildId()).playerAction().skipSong();
    }

    @Override
    public String describe(ButtonCommandContext context) {
        return null;
    }

    private boolean isExecutorInVoiceChannel(GuildVoiceState memberVoiceState) {
        return memberVoiceState != null && memberVoiceState.inAudioChannel() && memberVoiceState.getChannel() != null;
    }

    private boolean isExecutorAndBotAreInSameVoiceChannel(GuildVoiceState memberVoiceState, AudioManager audioManager) {
        return (isExecutorInVoiceChannel(memberVoiceState) && audioManager.isConnected());
    }
}
