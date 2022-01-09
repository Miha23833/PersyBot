package com.persybot.audio.audiomanager;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public interface AudioTrackFactory {

    List<AudioTrack> getAudioTracks(List<SongMetadata> songMetadata);

    AudioTrack getAudioTrack(SongMetadata songMetadata);
}
