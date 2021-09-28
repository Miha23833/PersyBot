package com.persybot.audio;

import com.persybot.audio.impl.AudioPlayerSendHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.List;

public interface AudioPlayer {
    AudioPlayerSendHandler getSendHandler();

    void setVolume(int volume);

    boolean hasNextTrack();

    boolean onPause();

    void loadAndPlay(String trackUrl);

    void resume();

    void pause();

    void stop();

    List<AudioTrackInfo> getQueue();

    void skip();
}
