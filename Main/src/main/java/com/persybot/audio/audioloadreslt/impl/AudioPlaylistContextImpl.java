package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

public class AudioPlaylistContextImpl implements AudioPlaylistContext {
    private final List<AudioTrack> tracks;
    private final TextChannel reqChannel;

    public AudioPlaylistContextImpl(List<AudioTrack> tracks, TextChannel reqChannel) {
        this.tracks = tracks;
        this.reqChannel = reqChannel;
    }

    @Override
    public List<AudioTrack> getTracks() {
        return this.tracks;
    }

    @Override
    public List<AudioTrackInfo> getTracksInfo() {
        return tracks.stream().map(AudioTrack::getInfo).collect(Collectors.toList());
    }

    @Override
    public TextChannel getRequestingChannel() {
        return this.reqChannel;
    }
}
