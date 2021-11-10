package com.persybot.audio.impl;

import com.persybot.audio.audioloadreslt.impl.DefaultAudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class GuildAudioPlayer implements com.persybot.audio.AudioPlayer {
    private final AudioPlayer audioPlayer;
    private final TrackSchedulerImpl scheduler;
    private final AudioPlayerSendHandler sendHandler;
    private final AudioPlayerManager musicManager;

    public GuildAudioPlayer(AudioPlayerManager manager) {
        this.musicManager = manager;
        this.audioPlayer = manager.createPlayer();

        this.scheduler = new TrackSchedulerImpl(this.audioPlayer);
        this.audioPlayer.addListener(this.scheduler);

        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }

    @Override
    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }

    @Override
    public void setVolume(int volume) {
        this.audioPlayer.setVolume(volume);
    }

    @Override
    public boolean hasNextTrack() {
        return scheduler.isEmpty();
    }

    @Override
    public boolean onPause() {
        return audioPlayer.isPaused();
    }

    @Override
    public void loadAndPlay(String trackUrl, TextChannel requestingChannel) {
        this.musicManager.loadItemOrdered(this, trackUrl, new DefaultAudioLoadResultHandler(scheduler, requestingChannel));
    }

    @Override
    public void resume() {
        audioPlayer.setPaused(false);
    }

    @Override
    public void pause() {
        audioPlayer.setPaused(true);
    }

    @Override
    public void stop() {
        scheduler.clearQueue();
        audioPlayer.stopTrack();
    }

    @Override
    public List<AudioTrackInfo> getQueue() {
        return scheduler.getQueuedTracksInfo();
    }

    @Override
    public void skip() {
        this.scheduler.stopRepeating();
        this.scheduler.nextTrack();
    }

    @Override
    public void repeat() {
        this.scheduler.repeatTrack();
    }

    @Override
    public void mixQueue() {
        this.scheduler.mixQueue();
    }
}