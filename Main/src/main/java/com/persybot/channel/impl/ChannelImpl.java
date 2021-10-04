package com.persybot.channel.impl;

import com.persybot.audio.AudioPlayer;
import com.persybot.audio.impl.GuildAudioPlayer;
import com.persybot.channel.Channel;
import com.persybot.channel.botaction.PlayerAction;
import com.persybot.channel.botaction.VoiceChannelAction;
import com.persybot.channel.botaction.impl.PlayerActionImpl;
import com.persybot.channel.botaction.impl.VoiceChannelActionImpl;
import com.persybot.db.model.impl.DiscordServerSettings;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public class ChannelImpl implements Channel {
    private final AudioPlayer audioPlayer;
    private final DiscordServerSettings serverSettings;
    private long lastPlayerMessageId = -1L;
    private final Guild discordServer;

    private final PlayerAction playerAction = new PlayerActionImpl(this);
    private final VoiceChannelAction voiceChannelAction = new VoiceChannelActionImpl(this);

    public ChannelImpl(AudioPlayerManager playerManager, DiscordServerSettings serverSettings, Guild discordServer) {
        this.audioPlayer = new GuildAudioPlayer(playerManager);
        audioPlayer.setVolume(serverSettings.getVolume());
        this.serverSettings = serverSettings;
        this.discordServer = discordServer;
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

    @Override
    public PlayerAction playerAction() {
        return playerAction;
    }

    @Override
    public VoiceChannelAction voiceChannelAction() {
        return voiceChannelAction;
    }

    @Override
    public Guild getGuild() {
        return discordServer;
    }
}
