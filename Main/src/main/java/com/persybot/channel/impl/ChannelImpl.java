package com.persybot.channel.impl;

import com.persybot.audio.GuildAudioPlayer;
import com.persybot.audio.impl.GuildAudioPlayerImpl;
import com.persybot.channel.Channel;
import com.persybot.channel.botaction.PlayerAction;
import com.persybot.channel.botaction.VoiceChannelAction;
import com.persybot.channel.botaction.impl.PlayerActionImpl;
import com.persybot.channel.botaction.impl.VoiceChannelActionImpl;
import com.persybot.db.entity.DiscordServer;
import com.persybot.logger.impl.PersyBotLogger;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChannelImpl implements Channel {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private GuildAudioPlayer audioPlayer;
    private final DiscordServer discordServer;
    private final Guild guild;
    private final AudioPlayerManager manager;

    private final PlayerAction playerAction = new PlayerActionImpl(this);
    private final VoiceChannelAction voiceChannelAction = new VoiceChannelActionImpl(this);

    public ChannelImpl(AudioPlayerManager playerManager, DiscordServer discordServer, Guild guild) {
        this.manager = playerManager;

        this.discordServer = discordServer;
        this.guild = guild;
    }

    @Override
    public boolean hasInitiatedAudioPlayer() {
        try {
            this.rwLock.readLock().lock();
            return this.audioPlayer != null;
        } finally {
            this.rwLock.readLock().unlock();
        }
    }

    @Override
    public GuildAudioPlayer getAudioPlayer() {
        try {
            rwLock.writeLock().lock();
            if (this.audioPlayer == null) {
                PersyBotLogger.BOT_LOGGER.info("Initialized new audio player in channel " + getGuild().getId());
                initAudioPlayer();
            }
            return this.audioPlayer;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public DiscordServer getDiscordServer() {
        return this.discordServer;
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
        return guild;
    }

    @Override
    public void destroyAudioPlayer() {
        try {
            rwLock.writeLock().lock();
            getGuild().getAudioManager().setSendingHandler(null);
            this.audioPlayer = null;
        } finally {
            rwLock.writeLock().unlock();
            PersyBotLogger.BOT_LOGGER.info("Audio player was destroyed in channel " + getGuild().getId());
        }
    }

    private void initAudioPlayer() {
        this.audioPlayer = new GuildAudioPlayerImpl(this, this.manager);
        audioPlayer.setVolume(discordServer.getSettings().getVolume());
        getGuild().getAudioManager().setSendingHandler(this.audioPlayer.getSendHandler());
    }
}
