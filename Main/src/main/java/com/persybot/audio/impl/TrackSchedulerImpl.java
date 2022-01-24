package com.persybot.audio.impl;

import com.persybot.audio.TrackScheduler;
import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.persybot.audio.audioloadreslt.AudioTrackContext;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TrackSchedulerImpl implements TrackScheduler {
    private final List<AudioTrackContext> trackQueue;

    public TrackSchedulerImpl() {
        trackQueue = new LinkedList<>();
    }

    @Override
    public void addTrack(AudioTrackContext track) {
        this.trackQueue.add(track);
    }

    @Override
    public void addPlaylist(AudioPlaylistContext playlist) {
        this.trackQueue.addAll(playlist.getTracks());

    }

    @Override
    public boolean isEmpty() {
        return this.trackQueue.isEmpty();
    }

    @Override
    public AudioTrackContext skipMultiple(int countOfSkips) {
        if (isEmpty() || this.trackQueue.size() <= countOfSkips) {
            this.trackQueue.clear();
            return null;
        }

        for (int i = 1; i < countOfSkips; i++) {
            this.trackQueue.remove(0);
        }
        return nextTrack();
    }

    @Override
    public AudioTrackContext nextTrack() {
        if (isEmpty()) {
            return null;
        }

        return this.trackQueue.remove(0);
    }

    @Override
    public void clear() {
        this.trackQueue.clear();
    }

    @Override
    public void shuffle() {
        Collections.shuffle(this.trackQueue);
    }

    @Override
    public List<String> queuedTracksTitles() {
        return this.trackQueue.stream().map(AudioTrackContext::getTrackPresent).collect(Collectors.toList());
    }
}
