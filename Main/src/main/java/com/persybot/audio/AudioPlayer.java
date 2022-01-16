package com.persybot.audio;

import com.persybot.audio.impl.AudioPlayerSendHandler;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public interface AudioPlayer {
    AudioPlayerSendHandler getSendHandler();

    void setVolume(int volume);

    boolean hasNextTrack();

    boolean isPaused();

    boolean isPlaying();

    void loadAndPlay(String trackUrl, TextChannel requestingChannel);

    void resume();

    void pause();

    void stop();

    List<String> getQueuedTracks();

    void skip();

    void skip(int countOfSkips);

    void repeat();

    void mixQueue();

    void setEqualizer(float[] bands);

    void removeEqualizer();
}
