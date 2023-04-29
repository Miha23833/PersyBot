package com.persybot.channel.botaction;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public interface VoiceChannelAction {
    void joinChannel(VoiceChannel channelToJoin);

    void leaveChannel();
}
