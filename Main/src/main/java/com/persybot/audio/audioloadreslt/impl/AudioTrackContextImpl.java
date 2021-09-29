package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

public class AudioTrackContextImpl implements AudioTrackContext {
    private final AudioTrack track;
    private final TextChannel reqChannel;

    public AudioTrackContextImpl(AudioTrack track, TextChannel reqChannel) {
        this.track = track;
        this.reqChannel = reqChannel;
    }

    @Override
    public AudioTrack getTrack() {
        return this.track;
    }

    @Override
    public TextChannel getRequestingChannel() {
        return this.reqChannel;
    }
}
