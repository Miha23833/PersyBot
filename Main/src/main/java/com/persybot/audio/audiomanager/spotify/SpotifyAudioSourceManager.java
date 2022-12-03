package com.persybot.audio.audiomanager.spotify;

import com.google.common.collect.Lists;
import com.persybot.audio.audiomanager.AudioTrackFactory;
import com.persybot.audio.audiomanager.SongMetadata;
import com.persybot.audio.audiomanager.youtube.LazyYoutubeAudioTrackFactory;
import com.persybot.logger.impl.PersyBotLogger;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpotifyAudioSourceManager implements AudioSourceManager {
    private static final String SPOTIFY_ADDRESS = "https://www.open.spotify.com";
    private static final String SPOTIFY_DOMAIN = "open.spotify.com";
    private static final Pattern TRACK_ID_PATTERN = Pattern.compile("^(?:http://|https://|)(?:www\\.|)(?:m\\.|)open.spotify\\.com/(track)/([a-zA-Z0-9-_]+)/?(?:\\?.*|)$");
    private static final Pattern PLAYLIST_ID_PATTERN = Pattern.compile("^(?:http://|https://|)(?:www\\.|)(?:m\\.|)open.spotify\\.com/(playlist)/([a-zA-Z0-9-_]+)/?(?:\\?.*|)$");

    private final SpotifyApi spotify;
    private final AudioTrackFactory audioTrackFactory;

    public SpotifyAudioSourceManager(SpotifyApi spotifyApi) throws ParseException {
        this.audioTrackFactory = new LazyYoutubeAudioTrackFactory(new YoutubeSearchProvider(), new YoutubeAudioSourceManager(true));
        this.spotify = spotifyApi;
    }

    @Override
    public String getSourceName() {
        return "spotify";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        try {
            URL url = new URL(reference.identifier);

            if (!StringUtils.equals(url.getHost(), SPOTIFY_DOMAIN)) {
                return null;
            }

            Matcher match = PLAYLIST_ID_PATTERN.matcher(url.toString());
            if (match.matches()) {
                return loadPlaylistFromYT(match.group(2));
            }

            match = TRACK_ID_PATTERN.matcher(url.toString());
            if (match.matches()) {
                return loadTrackFromYT(match.group(2));
            }
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException | org.apache.hc.core5.http.ParseException | SpotifyWebApiException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        throw new UnsupportedOperationException("encodeTrack is unsupported.");
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        throw new UnsupportedOperationException("decodeTrack is unsupported.");
    }

    @Override
    public void shutdown() {}

    private AudioItem loadPlaylistFromYT(String playlistId) throws org.apache.hc.core5.http.ParseException, SpotifyWebApiException, IOException {
        Playlist playlist = spotify.getPlaylist(playlistId).build().execute();

        if (playlist == null || playlist.getTracks().getTotal() == 0) return null;

        List<String> trackIds = Arrays.stream(playlist.getTracks().getItems())
                .map(playlistTrack -> playlistTrack.getTrack().getId())
                .collect(Collectors.toList());

        List<AudioTrack> result = new ArrayList<>();

        for (List<String> trackIdSubList: Lists.partition(trackIds, 50)) {
            Track[] spotifyTracks = spotify.getSeveralTracks(trackIdSubList.toArray(new String[0])).build().execute();

            result.addAll(Arrays.stream(spotifyTracks).parallel().map(this::loadFromYT).collect(Collectors.toList()));
        }

        return new BasicAudioPlaylist(playlist.getName(), result, null, false);
    }

    private AudioTrack loadTrackFromYT(String id) throws org.apache.hc.core5.http.ParseException, SpotifyWebApiException, IOException {
        Track spotifyTrack = spotify.getTrack(id).build().execute();
        return loadFromYT(spotifyTrack);
    }

    private String collectArtists(ArtistSimplified[] artists) {
        return Arrays.stream(artists).map(ArtistSimplified::getName).collect(Collectors.joining(", "));
    }

    private AudioTrack loadFromYT(Track track) {
        SongMetadata metadata = getSongMetaData(track);
        AudioTrackInfo ati = new AudioTrackInfo(metadata.getName(), metadata.getArtist(),
                metadata.getDuration(), "ytsearch:" + metadata.getArtist() + " - " + metadata.getName(), false, metadata.getUrl());
        return audioTrackFactory.getAudioTrack(ati);
    }

    private SongMetadata getSongMetaData(Track track) {
        String firstArtistName = track.getArtists().length == 0 ? "" : collectArtists(track.getArtists());
        return new SongMetadata(track.getName(), firstArtistName, track.getDurationMs(), SPOTIFY_ADDRESS);
    }
}
