package com.persybot.audio;

import com.persybot.audio.impl.AudioPlayerSendHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public interface GuildAudioPlayer {
    AudioPlayerSendHandler getSendHandler();

    void setVolume(int volume);

    boolean isPaused();

    boolean isPlaying();

    void scheduleTrack(String trackUrl, TextChannel requestingChannel);

    void playTrack(AudioTrack track);

    void resume();

    void pause();

    void stop();

    List<String> getQueuedTracks();

    void skip();

    void skip(int countOfSkips);

    void repeat();

    void mixQueue();

    void setEqualizer(float[] bands);
}
