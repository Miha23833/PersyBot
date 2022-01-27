package com.persybot.audio.impl;

import com.persybot.audio.GuildAudioPlayer;
import com.persybot.audio.PlayerStateSender;
import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.persybot.audio.audioloadreslt.impl.DefaultAudioLoadResultHandler;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.service.MessageType;
import com.persybot.utils.QueueSuccessActionTemplates;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuildAudioPlayerImpl extends AudioEventAdapter implements GuildAudioPlayer, PlayerStateSender {
    private final AudioPlayerManager musicManager;
    private final AudioPlayer audioPlayer;
    private final AudioPlayerSendHandler sendHandler;
    private boolean loop = false;
    private byte trackExceptionToDropPlaylistDown = 0;

    private final TrackSchedulerImpl trackScheduler;

    public GuildAudioPlayerImpl(AudioPlayerManager manager) {
        this.musicManager = manager;
        this.audioPlayer = manager.createPlayer();

        this.trackScheduler = new TrackSchedulerImpl();
        this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);

        this.audioPlayer.addListener(this);
    }


    @Override
    public AudioPlayerSendHandler getSendHandler() {
        return this.sendHandler;
    }

    @Override
    public void setVolume(int volume) {
        this.audioPlayer.setVolume(volume);
    }

    @Override
    public boolean isPaused() {
        return this.audioPlayer.isPaused();
    }

    @Override
    public boolean isPlaying() {
        return this.audioPlayer.getPlayingTrack() != null;
    }

    @Override
    public void scheduleTrack(String trackUrl, TextChannel requestingChannel) {
        this.musicManager.loadItemOrdered(this, trackUrl, new DefaultAudioLoadResultHandler(this, this.trackScheduler, requestingChannel, trackUrl));
    }

    @Override
    public void playTrack(AudioTrack track) {
        this.audioPlayer.playTrack(track);
    }

    @Override
    public void resume() {
        this.audioPlayer.setPaused(false);
    }

    @Override
    public void pause() {
        this.audioPlayer.setPaused(true);
    }

    @Override
    public void stop() {
        this.trackScheduler.clear();
        this.audioPlayer.destroy();
        this.resume();
    }

    @Override
    public List<String> getQueuedTracks() {
        return this.trackScheduler.queuedTracksTitles();
    }

    @Override
    public void skip() {
        this.skip(1);
    }

    @Override
    public void skip(int countOfSkips) {
        this.loop = false;
        AudioTrackContext newTrack = this.trackScheduler.skipMultiple(countOfSkips);
        if (newTrack == null) {
            stop();
        } else {
            newTrack.getRequestingChannel()
                    .sendMessage(getPlayingTrackMessage(newTrack.getTrackPresent(), isPaused()))
                    .queue(QueueSuccessActionTemplates.addToSelfCleaner(MessageType.PLAYER_NOW_PLAYING));
            this.audioPlayer.startTrack(newTrack.getTrack(), false);
        }
    }

    @Override
    public void repeat() {
        this.loop = true;
    }

    @Override
    public void mixQueue() {
        this.trackScheduler.shuffle();
    }

    @Override
    public void setEqualizer(float[] bands) {
        EqualizerFactory eq = new EqualizerFactory();

        if (bands == null || bands.length != 15) {
            throw new IllegalArgumentException("Incorrect equalizer length. It must be 15" );
        }

        for (int i = 0; i < bands.length; i++) {
            float band = bands[i];
            if (band > 1 || band < -0.25f) {
                throw new IllegalArgumentException("Incorrect equalizer band value. Band index: " + i + ", band value: " + band);
            }
            eq.setGain(i, band);
        }
        this.audioPlayer.setFilterFactory(eq);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (this.loop) {
                this.audioPlayer.playTrack(track.makeClone());
            }
            else {
                if (this.trackScheduler.isEmpty()) {
                    stop();
                    return;
                }
                AudioTrackContext newTrack = this.trackScheduler.nextTrack();

                this.audioPlayer.playTrack(newTrack.getTrack());
                newTrack.getRequestingChannel()
                        .sendMessage(getPlayingTrackMessage(newTrack.getTrackPresent(), isPaused()))
                        .queue(QueueSuccessActionTemplates.addToSelfCleaner(MessageType.PLAYER_NOW_PLAYING));
            }
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        this.trackExceptionToDropPlaylistDown = 0;
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        this.trackExceptionToDropPlaylistDown++;

        if (this.trackExceptionToDropPlaylistDown >= 3) {
            this.stop();
        }

        PersyBotLogger.BOT_LOGGER.error("Track identifier: " + track.getIdentifier(), exception);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        String stackTraceStr = Arrays.stream(stackTrace).map(StackTraceElement::toString).collect(Collectors.joining("\n"));

        PersyBotLogger.BOT_LOGGER.error("Track got stuck for " + thresholdMs + "ms. Track identifier: " + track.getIdentifier() + "\n" + stackTraceStr);
    }
}
