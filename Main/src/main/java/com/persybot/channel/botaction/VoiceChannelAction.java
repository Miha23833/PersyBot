package com.persybot.channel.botaction;

import net.dv8tion.jda.api.entities.VoiceChannel;

public interface VoiceChannelAction {
    void joinChannel(VoiceChannel channelToJoin);

    void leaveChannel();
}
