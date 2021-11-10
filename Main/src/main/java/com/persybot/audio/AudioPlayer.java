package com.persybot.audio;

import com.persybot.audio.impl.AudioPlayerSendHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public interface AudioPlayer {
    AudioPlayerSendHandler getSendHandler();

    void setVolume(int volume);

    boolean hasNextTrack();

    boolean onPause();

    boolean isPlaying();

    void loadAndPlay(String trackUrl, TextChannel requestingChannel);

    void resume();

    void pause();

    void stop();

    List<AudioTrackInfo> getQueue();

    void skip();

    void repeat();

    void mixQueue();
}
