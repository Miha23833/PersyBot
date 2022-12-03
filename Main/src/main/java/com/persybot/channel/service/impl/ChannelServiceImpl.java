package com.persybot.channel.service.impl;

import com.persybot.audio.audiomanager.spotify.SpotifyAudioSourceManager;
import com.persybot.audio.audiomanager.youtube.LazyYoutubeAudioSourceManager;
import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.spotify.api.SpotifyApiDataService;
import com.persybot.youtube.api.YoutubeApiDataService;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
                    playerManager.registerSourceManager(new SpotifyAudioSourceManager(ServiceAggregator.getInstance().get(SpotifyApiDataService.class).getApi()));
                    playerManager.registerSourceManager(new LazyYoutubeAudioSourceManager(ServiceAggregator.getInstance().get(YoutubeApiDataService.class).getApi()));
                    playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
                    playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());

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
        return channels.get(id);
    }

    @Override
    public void addChannel(Long id, Channel channel) {
        this.channels.put(id, channel);
    }

    @Override
    public AudioPlayerManager getAudioPlayerManager() {
        return this.audioPlayerManager;
    }
}
