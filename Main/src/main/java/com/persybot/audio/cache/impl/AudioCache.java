package com.persybot.audio.cache.impl;

import com.persybot.logger.impl.PersyBotLogger;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AudioCache {
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static AudioCache INSTANCE;

    private final ConcurrentHashMap<String, TrackContext> cache;

    private AudioCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    public static AudioCache getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new AudioCache();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    public void addTrack(AudioTrack track) {
        if (track == null) {
            PersyBotLogger.BOT_LOGGER.error("Track is null");
            return;
        } else if (track.getIdentifier() == null) {
            PersyBotLogger.BOT_LOGGER.error("Track identifier is null");
            return;
        }

        TrackContext ctx = new TrackContext(track, System.currentTimeMillis());

        cache.put(ctx.getIdentifier(), ctx);
    }

    public AudioTrack getTrack(String identifier) {
        if (exists(identifier)){
            return cache.get(identifier).getTrack();
        }
        return null;
    }

    public boolean exists(String identifier) {
        return cache.containsKey(identifier);
    }

    public void removeTrack(String identifier) {
        cache.remove(identifier);

    }

    public void removeExpired() {
        final long timeSnapshot = System.currentTimeMillis();
        cache.keys().asIterator().forEachRemaining(identifier -> {
            if ( cache.get(identifier).expirationTime > timeSnapshot) {
                cache.remove(identifier);
            }
        });
    }

    public void clearCache() {
        cache.clear();
    }

    private static class TrackContext {
        private final AudioTrack track;
        private final String identifier;
        public long expirationTime;


        public TrackContext(AudioTrack track, long expirationTime) {
            this.expirationTime = expirationTime;
            this.track = track;
            this.identifier = track.getIdentifier();
        }

        public AudioTrack getTrack() {
            return track;
        }

        public String getIdentifier() {
            return identifier;
        }
    }
}
