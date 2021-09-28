package com.persybot.channel.botaction.impl;

import com.persybot.channel.Channel;
import com.persybot.channel.botaction.VoiceChannelAction;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class VoiceChannelActionImpl extends AbstractBotAction implements VoiceChannelAction {

    public VoiceChannelActionImpl(Channel actingChannel) {
        super(actingChannel);
    }

    @Override
    public void joinChannel(VoiceChannel channelToConnect) {
        this.actingChannel.getGuild().getAudioManager().openAudioConnection(channelToConnect);
    }

    @Override
    public void leaveChannel() {
        actingChannel.getGuild().getAudioManager().closeAudioConnection();
    }
}
