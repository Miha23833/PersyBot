package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import static com.persybot.utils.DateTimeUtils.toTimeDuration;

public class AudioTrackContextImpl implements AudioTrackContext {
    private final AudioTrack track;
    private final TextChannel reqChannel;
    private final String trackPresent;

    public AudioTrackContextImpl(AudioTrack track, TextChannel reqChannel) {
        this.track = track;
        this.reqChannel = reqChannel;
        this.trackPresent = getAudioTrackPresent(track.getInfo());
    }

    @Override
    public AudioTrack getTrack() {
        return this.track;
    }

    @Override
    public TextChannel getRequestingChannel() {
        return this.reqChannel;
    }

    @Override
    public String getTrackPresent() {
        return trackPresent;
    }


    private String getAudioTrackPresent(AudioTrackInfo info) {
        String builder = info.author + " - " + info.title +
                " (" + toTimeDuration(info.length) + ")";
        return builder;
    }
}
