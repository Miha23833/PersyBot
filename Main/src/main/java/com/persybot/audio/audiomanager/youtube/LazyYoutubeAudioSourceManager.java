package com.persybot.audio.audiomanager.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.persybot.audio.audiomanager.AudioTrackFactory;
import com.persybot.utils.URLUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.apache.commons.collections4.ListUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LazyYoutubeAudioSourceManager implements AudioSourceManager {
    private static final List<String> YOUTUBE_SNIPPET_PART = Collections.singletonList("snippet");
    private static final List<String> YOUTUBE_CONTENT_DETAILS_PART = Collections.singletonList("contentDetails");

    private static final Pattern TRACK_ID_PATTERN = Pattern.compile("(?:https?:)?(?://)?(?:[\\dA-Z-]+\\.)?(?:youtu\\.be/|youtube(?:-nocookie)?\\.com\\S*?[^\\w\\s-])(?<id>[\\w-]{11})(?=[^\\w-]|$)(?![?=&+%\\w.\\-]*(?:['\"][^<>]*>|</a>))");
    private static final Pattern PLAYLIST_ID_PATTERN = Pattern.compile("(?:https?:)?(?://)?(?:[\\dA-Z-]+\\.)?(?:youtu\\.be/|youtube(?:-nocookie)?\\.com\\S*?[^\\w\\s-])((play)?list=)(?<id>[\\w\\d\\-]+)");

    private static final String CONTENT_ID_GROUP = "id";


    private final YouTube ytApi;
    private final AudioTrackFactory audioTrackFactory;
    private final YoutubeAudioSourceManager ytSourceManager;

    // TODO: move to config
    private final int playlistItemsLimit = 50;

    public LazyYoutubeAudioSourceManager(YouTube ytApi) {
        this.ytApi = ytApi;
        this.ytSourceManager = new YoutubeAudioSourceManager();
        this.audioTrackFactory = new LazyYoutubeAudioTrackFactory(new YoutubeSearchProvider(), ytSourceManager);
    }

    @Override
    public String getSourceName() {
        return "youtube_lazy";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        try {
            if (!URLUtil.isUrl(reference.identifier)) {
                return loadTrackFromYtByTitle(reference.identifier);
            }
            Matcher match = PLAYLIST_ID_PATTERN.matcher(reference.identifier);
            if (match.find()) {
                return loadPlaylistFromYT(match.group(CONTENT_ID_GROUP));
            }
            match = TRACK_ID_PATTERN.matcher(reference.identifier);

            if (match.find()) {
                return loadTrackFromYT(match.group(CONTENT_ID_GROUP));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return this.ytSourceManager.isTrackEncodable(track);
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        this.ytSourceManager.encodeTrack(track, output);
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return this.ytSourceManager.decodeTrack(trackInfo, input);
    }

    @Override
    public void shutdown() {
        this.ytSourceManager.shutdown();
    }

    private AudioItem loadPlaylistFromYT(String playlistId) throws IOException {
        PlaylistListResponse playlistRsp = ytApi.playlists()
                .list(YOUTUBE_SNIPPET_PART)
                .setId(Collections.singletonList(playlistId))
                .setMaxResults(1L)
                .execute();
        Playlist playlist = playlistRsp.getItems().get(0);

        List<PlaylistItem> playlistItems = ytApi.playlistItems()
                .list(YOUTUBE_CONTENT_DETAILS_PART)
                .setPlaylistId(playlistId)
                .setMaxResults((long) playlistItemsLimit)
                .execute().getItems();

        List<Video> playlistVideos = ytApi.videos()
                .list(ListUtils.union(YOUTUBE_SNIPPET_PART, YOUTUBE_CONTENT_DETAILS_PART))
                .setId(playlistItems.stream().map(it -> it.getContentDetails().getVideoId()).collect(Collectors.toList()))
                .execute().getItems();

        List<AudioTrack> result = playlistVideos.stream().map(audioTrackFactory::getAudioTrack).collect(Collectors.toList());

        return new BasicAudioPlaylist(playlist.getSnippet().getTitle(), result, null, false);
    }

    private AudioTrack loadTrackFromYT(String id) throws IOException {
        Video ytVideo = ytApi.videos()
                .list(ListUtils.union(YOUTUBE_SNIPPET_PART, YOUTUBE_CONTENT_DETAILS_PART))
                .setMaxResults(1L)
                .setId(Collections.singletonList(id))
                .execute()
                .getItems()
                .get(0);
        return audioTrackFactory.getAudioTrack(ytVideo);
    }

    private AudioTrack loadTrackFromYtByTitle(String title) throws IOException {
        SearchListResponse searchRsp = ytApi.search()
                .list(YOUTUBE_SNIPPET_PART)
                .setMaxResults(1L)
                .setQ(title)
                .execute();

        if (searchRsp.getItems().isEmpty()) {
            return null;
        }
        return loadTrackFromYT(searchRsp.getItems().get(0).getId().getVideoId());

    }
}
