package com.persybot.channel.service;

import com.persybot.audio.AudioPlayer;
import com.persybot.audio.impl.GuildAudioPlayer;
import com.persybot.channel.Channel;
import com.persybot.db.model.impl.DiscordServerSettings;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.jetbrains.annotations.NotNull;

public class ChannelImpl implements Channel {
    private final AudioPlayer audioPlayer;
    private final DiscordServerSettings serverSettings;
    private long lastPlayerMessageId = -1L;



    public ChannelImpl(AudioPlayerManager playerManager, DiscordServerSettings serverSettings) {
        this.audioPlayer = new GuildAudioPlayer(playerManager);
        this.serverSettings = serverSettings;
    }

    @Override
    public AudioPlayer getAudioPlayer() {
        return this.audioPlayer;
    }

    @Override
    public DiscordServerSettings getServerSettings() {
        return this.serverSettings;
    }

    @Override
    public long getLastPlayerMessageId() {
        return lastPlayerMessageId;
    }

    @Override
    public void setLastPlayerMessageId(@NotNull Long id) {
        this.lastPlayerMessageId = id;
    }
}
