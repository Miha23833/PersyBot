package com.persybot.channel;

import com.persybot.audio.AudioPlayer;
import com.persybot.channel.botaction.PlayerAction;
import com.persybot.channel.botaction.VoiceChannelAction;
import com.persybot.db.entity.DiscordServerSettings;
import net.dv8tion.jda.api.entities.Guild;

public interface Channel {
    AudioPlayer getAudioPlayer();

    DiscordServerSettings getServerSettings();

    long getLastPlayerMessageId();

    void setLastPlayerMessageId(Long id);

    PlayerAction playerAction();

    VoiceChannelAction voiceChannelAction();

    Guild getGuild();
}
