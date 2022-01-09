package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.persybot.audio.audioloadreslt.AudioTrackContext;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class AudioPlaylistContextImpl implements AudioPlaylistContext {
    private final List<AudioTrackContext> tracks;
    private final TextChannel reqChannel;

    public AudioPlaylistContextImpl(List<AudioTrackContext> tracks, TextChannel reqChannel) {
        this.tracks = tracks;
        this.reqChannel = reqChannel;
    }

    @Override
    public List<AudioTrackContext> getTracks() {
        return this.tracks;
    }

    @Override
    public TextChannel getRequestingChannel() {
        return this.reqChannel;
    }
}
