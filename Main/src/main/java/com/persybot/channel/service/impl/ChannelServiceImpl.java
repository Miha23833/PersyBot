package com.persybot.channel.service.impl;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelImpl;
import com.persybot.channel.service.ChannelService;
import com.persybot.db.model.impl.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChannelServiceImpl implements ChannelService {
    private final Map<Long, Channel> channels;
    private final AudioPlayerManager audioPlayerManager;
    private static ChannelServiceImpl INSTANCE;
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private ChannelServiceImpl(AudioPlayerManager audioPlayerManager){
        this.channels = new ConcurrentHashMap<>();
        this.audioPlayerManager = audioPlayerManager;
    }

    public static ChannelServiceImpl getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

                    AudioSourceManagers.registerRemoteSources(playerManager);
                    AudioSourceManagers.registerLocalSource(playerManager);
                    INSTANCE = new ChannelServiceImpl(playerManager);
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    @Override
    public Channel getChannel(Long id) {
        return channels.computeIfAbsent(id, this::newInstance);
    }

    private Channel newInstance(Long id) {
        return new ChannelImpl(audioPlayerManager, getDSSFromDbOrCreateDefault(id));
    }

    private DiscordServerSettings getDSSFromDbOrCreateDefault(long id) {
        DiscordServerSettings serverSettings;
        try {
            serverSettings = ServiceAggregatorImpl.getInstance().getService(DBService.class).get(DiscordServerSettings.class, id);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            serverSettings = new DiscordServerSettings(id);
            ServiceAggregatorImpl.getInstance().getService(DBService.class).add(serverSettings);
        }
        if (serverSettings == null) {
            serverSettings = new DiscordServerSettings(id);
            ServiceAggregatorImpl.getInstance().getService(DBService.class).add(serverSettings);
        }
        return serverSettings;
    }
}
