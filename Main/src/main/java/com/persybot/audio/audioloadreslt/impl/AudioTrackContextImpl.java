package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.utils.URLUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URISyntaxException;

import static com.persybot.utils.DateTimeUtils.toTimeDuration;
import static com.persybot.utils.URLUtil.isYoutubeDomain;

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
