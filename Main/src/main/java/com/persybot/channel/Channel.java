package com.persybot.channel;

import com.persybot.audio.GuildAudioPlayer;
import com.persybot.channel.botaction.PlayerAction;
import com.persybot.channel.botaction.VoiceChannelAction;
import com.persybot.db.entity.DiscordServerSettings;
import net.dv8tion.jda.api.entities.Guild;

public interface Channel {
    boolean hasInitiatedAudioPlayer();
    GuildAudioPlayer getAudioPlayer();

    DiscordServerSettings getServerSettings();

    PlayerAction playerAction();

    VoiceChannelAction voiceChannelAction();

    Guild getGuild();

    void destroyAudioPlayer();
}
