package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

public class AudioPlaylistContextImpl implements AudioPlaylistContext {
    private final List<AudioTrackContext> tracks;

    public AudioPlaylistContextImpl(List<AudioTrack> trackList, TextChannel rspChannel) {
        this.tracks = trackList.stream().map(tr -> new AudioTrackContextImpl(tr, rspChannel)).collect(Collectors.toList());
    }

    @Override
    public List<AudioTrackContext> getTracks() {
        return this.tracks;
    }
}
