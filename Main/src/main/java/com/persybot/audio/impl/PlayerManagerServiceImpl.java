package com.persybot.audio.impl;

import com.persybot.audio.PlayerManagerService;
import com.persybot.audio.audioloadreslt.DefaultAudioLoadResultHandler;
import com.persybot.db.model.impl.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PlayerManagerServiceImpl implements PlayerManagerService {
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static PlayerManagerServiceImpl INSTANCE;

    private final ServiceAggregatorImpl serviceAggregator;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private PlayerManagerServiceImpl() {
        this.musicManagers = new ConcurrentHashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.serviceAggregator = ServiceAggregatorImpl.getInstance();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            DiscordServerSettings serverSettings = null;
            try {
                serverSettings = serviceAggregator.getService(DBService.class).get(DiscordServerSettings.class, guildId);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                PersyBotLogger.BOT_LOGGER.error(e);
            }
            if (serverSettings == null) {
                serverSettings = new DiscordServerSettings(guildId);
                serviceAggregator.getService(DBService.class).add(serverSettings);
            }

            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, serverSettings);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    @Override
    public void loadAndPlay(TextChannel channel, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new DefaultAudioLoadResultHandler(musicManager, channel));
    }

    @Override
    public void skip(Guild guild) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);
        musicManager.scheduler.nextTrack();
    }

    @Override
    public void setVolume(Guild guild, int volume) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        DiscordServerSettings serverSettings = null;
        try {
            serverSettings = serviceAggregator.getService(DBService.class).get(DiscordServerSettings.class, guild.getIdLong());
            serverSettings.setVolume(volume);
            serviceAggregator.getService(DBService.class).update(serverSettings);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
        }
        if (serverSettings == null) {
            serverSettings = new DiscordServerSettings(guild.getIdLong(), volume);
            serviceAggregator.getService(DBService.class).add(serverSettings);
        }

        musicManager.audioPlayer.setVolume(volume);
    }

    public static PlayerManagerServiceImpl getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new PlayerManagerServiceImpl();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

}