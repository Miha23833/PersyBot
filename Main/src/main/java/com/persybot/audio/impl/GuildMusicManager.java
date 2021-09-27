package com.persybot.audio.impl;

import com.persybot.db.model.impl.DiscordServerSettings;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
    public final AudioPlayer audioPlayer;

    public final TrackScheduler scheduler;

    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager, DiscordServerSettings serverSettings) {
        this.audioPlayer = manager.createPlayer();
        this.audioPlayer.setVolume(serverSettings.getVolume());
        this.scheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.scheduler);
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }
}