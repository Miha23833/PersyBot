package com.persybot.audio.audiomanager.youtube;

import com.persybot.logger.impl.PersyBotLogger;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

public class LazyYoutubeAudioTrack extends DelegatedAudioTrack {
    private static final int SEARCH_RESULTS_LIMIT = 5;

    private final YoutubeAudioSourceManager sourceManager;
    private final YoutubeSearchProvider ytSearchProvider;
    private final AudioTrackInfo initialAudioTrackInfo;

    private YoutubeAudioTrack realTrack;

    public LazyYoutubeAudioTrack(AudioTrackInfo initialAudioTrackInfo, YoutubeAudioSourceManager sourceManager,
                                 YoutubeSearchProvider ytSearchProvider) {
        super(initialAudioTrackInfo);
        this.initialAudioTrackInfo = initialAudioTrackInfo;
        this.sourceManager = sourceManager;
        this.ytSearchProvider = ytSearchProvider;

        this.realTrack = null;
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        if (realTrack == null) {
            this.realTrack = getTrack();
        }

        if (this.realTrack != null) {
            this.processDelegate(realTrack, executor);
        } else {
            throw new RuntimeException("Couldn't find a track on YouTube.");
        }
    }

    @Override
    public String getIdentifier() {
        return this.getInfo().identifier;
    }

    @Override
    public AudioTrackInfo getInfo() {
        if (realTrack != null) {
            return realTrack.getInfo();
        }

        return initialAudioTrackInfo;
    }

    @Override
    public boolean isSeekable() {
        if (realTrack != null) {
            return realTrack.isSeekable();
        }

        return false;
    }

    @Override
    public AudioTrack makeClone() {
        LazyYoutubeAudioTrack clone = new LazyYoutubeAudioTrack(this.getInfo(), sourceManager, ytSearchProvider);
        clone.setUserData(this.getUserData());

        return clone;
    }

    private YoutubeAudioTrack getTrack() {
        AudioTrackInfo trackInfo = this.getInfo();
        String query = trackInfo.title + " " + trackInfo.author;

        AudioItem audioItem = ytSearchProvider.loadSearchResult(query, info -> new YoutubeAudioTrack(info, sourceManager));

        if (audioItem == AudioReference.NO_TRACK) {
            return null;
        } else if (audioItem instanceof AudioPlaylist) {
            AudioPlaylist audioPlaylist = (AudioPlaylist) audioItem;

            // The number of matches is limited to reduce the chances of matching against
            // less than optimal results.
            // The best match is the one that has the smallest track duration delta.
            YoutubeAudioTrack bestMatch = audioPlaylist.getTracks().stream().limit(SEARCH_RESULTS_LIMIT)
                    .map(t -> (YoutubeAudioTrack) t).min((o1, o2) -> {
                        long o1TimeDelta = Math.abs(o1.getDuration() - trackInfo.length);
                        long o2TimeDelta = Math.abs(o2.getDuration() - trackInfo.length);

                        return (int) (o1TimeDelta - o2TimeDelta);
                    }).orElse(null);

            return bestMatch;
        } else if (audioItem instanceof YoutubeAudioTrack) {
            return (YoutubeAudioTrack) audioItem;
        } else {
            PersyBotLogger.BOT_LOGGER.warn("Unknown AudioItem '{}' returned by YoutubeSearchProvider.", audioItem);
            return null;
        }
    }
}