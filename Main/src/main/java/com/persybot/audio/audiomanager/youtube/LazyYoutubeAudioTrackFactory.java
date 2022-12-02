package com.persybot.audio.audiomanager.youtube;

import com.google.api.services.youtube.model.Video;
import com.persybot.audio.audiomanager.AudioTrackFactory;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.time.Duration;

public class LazyYoutubeAudioTrackFactory implements AudioTrackFactory {

    private final YoutubeSearchProvider ytSearchProvider;
    private final YoutubeAudioSourceManager ytAudioSourceManager;

    public LazyYoutubeAudioTrackFactory(YoutubeSearchProvider ytSearchProvider,
                                        YoutubeAudioSourceManager ytAudioSourceManager) {
        this.ytSearchProvider = ytSearchProvider;
        this.ytAudioSourceManager = ytAudioSourceManager;
    }

    @Override
    public LazyYoutubeAudioTrack getAudioTrack(Video ytVideo) {
        String author = ytVideo.getSnippet().getChannelTitle();
        String title = ytVideo.getSnippet().getTitle();
        if (title.chars().filter(ch -> ch == '-').count() == 1 && !title.startsWith("-") && !title.endsWith("-")) {
            author = title.substring(0, title.indexOf('-')).trim();
            title = title.substring(title.indexOf('-') + 1).trim();
        } else if (title.chars().filter(ch -> ch == '—').count() == 1 && !title.startsWith("—") && !title.endsWith("—")) {
            author = title.substring(0, title.indexOf('—')).trim();
            title = title.substring(title.indexOf('—') + 1).trim();
        }
        long durationMillis = Duration.parse(ytVideo.getContentDetails().getDuration()).toMillis();
        AudioTrackInfo info = new AudioTrackInfo(title, author, durationMillis, ytVideo.getId(), false, ("https://www.youtube.com/watch?v=" + ytVideo.getId()));
        return new LazyYoutubeAudioTrack(info, ytAudioSourceManager, ytSearchProvider);
    }

    @Override
    public LazyYoutubeAudioTrack getAudioTrack(AudioTrackInfo ati) {
        return new LazyYoutubeAudioTrack(ati, ytAudioSourceManager, ytSearchProvider);
    }
}