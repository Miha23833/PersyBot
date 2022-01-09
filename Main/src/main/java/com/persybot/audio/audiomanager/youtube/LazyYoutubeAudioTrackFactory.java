package com.persybot.audio.audiomanager.youtube;

import com.persybot.audio.audiomanager.AudioTrackFactory;
import com.persybot.audio.audiomanager.SongMetadata;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.List;
import java.util.stream.Collectors;

public class LazyYoutubeAudioTrackFactory implements AudioTrackFactory {

    private final YoutubeSearchProvider ytSearchProvider;
    private final YoutubeAudioSourceManager ytAudioSourceManager;

    public LazyYoutubeAudioTrackFactory(YoutubeSearchProvider ytSearchProvider,
                                        YoutubeAudioSourceManager ytAudioSourceManager) {
        this.ytSearchProvider = ytSearchProvider;
        this.ytAudioSourceManager = ytAudioSourceManager;
    }

    @Override
    public List<AudioTrack> getAudioTracks(List<SongMetadata> songMetadata) {
        return songMetadata.stream().map(this::getAudioTrack).collect(Collectors.toList());
    }

    @Override
    public AudioTrack getAudioTrack(SongMetadata songMetadata) {
        AudioTrackInfo ati = new AudioTrackInfo(songMetadata.getName(), songMetadata.getArtist(),
                songMetadata.getDuration(), "ytsearch:" + songMetadata.getArtist() + " - " + songMetadata.getName(), false, songMetadata.getUrl());
        return new LazyYoutubeAudioTrack(ati, ytAudioSourceManager, ytSearchProvider);
    }
}