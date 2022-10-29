package com.persybot.channel;

import com.persybot.audio.GuildAudioPlayer;
import com.persybot.channel.botaction.PlayerAction;
import com.persybot.channel.botaction.VoiceChannelAction;
import com.persybot.db.entity.DiscordServer;
import net.dv8tion.jda.api.entities.Guild;

public interface Channel {
    boolean hasInitiatedAudioPlayer();
    GuildAudioPlayer getAudioPlayer();

    DiscordServer getDiscordServer();

    PlayerAction playerAction();

    VoiceChannelAction voiceChannelAction();

    Guild getGuild();

    void destroyAudioPlayer();
}
