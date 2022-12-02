package com.persybot.audio.audiomanager;

import com.google.api.services.youtube.model.Video;
import com.persybot.audio.audiomanager.youtube.LazyYoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

public interface AudioTrackFactory {
    LazyYoutubeAudioTrack getAudioTrack(AudioTrackInfo songMetadata);
    LazyYoutubeAudioTrack getAudioTrack(Video songMetadata);
}
