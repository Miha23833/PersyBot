package com.persybot.audio.audioloadreslt;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public interface AudioPlaylistContext {
    List<AudioTrack> getTracks();

    List<AudioTrackInfo> getTracksInfo();

    TextChannel getRequestingChannel();
}
