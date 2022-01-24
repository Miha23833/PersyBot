package com.persybot.channel.impl;

import com.persybot.audio.GuildAudioPlayer;
import com.persybot.audio.impl.GuildAudioPlayerImpl;
import com.persybot.channel.Channel;
import com.persybot.channel.botaction.PlayerAction;
import com.persybot.channel.botaction.VoiceChannelAction;
import com.persybot.channel.botaction.impl.PlayerActionImpl;
import com.persybot.channel.botaction.impl.VoiceChannelActionImpl;
import com.persybot.db.entity.DiscordServerSettings;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

public class ChannelImpl implements Channel {
    private final GuildAudioPlayer audioPlayer;
    private final DiscordServerSettings serverSettings;
    private final Guild discordServer;

    private final PlayerAction playerAction = new PlayerActionImpl(this);
    private final VoiceChannelAction voiceChannelAction = new VoiceChannelActionImpl(this);

    public ChannelImpl(AudioPlayerManager playerManager, DiscordServerSettings serverSettings, Guild discordServer) {
        this.audioPlayer = new GuildAudioPlayerImpl(playerManager);
        audioPlayer.setVolume(serverSettings.getVolume());
        this.serverSettings = serverSettings;
        this.discordServer = discordServer;
    }

    @Override
    public GuildAudioPlayer getAudioPlayer() {
        return this.audioPlayer;
    }

    @Override
    public DiscordServerSettings getServerSettings() {
        return this.serverSettings;
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
