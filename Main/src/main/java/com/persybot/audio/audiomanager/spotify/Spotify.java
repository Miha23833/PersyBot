package com.persybot.audio.audiomanager.spotify;

import com.persybot.logger.impl.PersyBotLogger;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Spotify implements AudioSourceManager {
    private static final String SPOTIFY_DOMAIN = "open.spotify.com";
    private static final Pattern TRACK_ID_PATTERN = Pattern.compile("^(?:http://|https://|)(?:www\\.|)(?:m\\.|)open.spotify\\.com/(track)/([a-zA-Z0-9-_]+)/?(?:\\?.*|)$");
    private static final Pattern PLAYLIST_ID_PATTERN = Pattern.compile("^(?:http://|https://|)(?:www\\.|)(?:m\\.|)open.spotify\\.com/(playlist)/([a-zA-Z0-9-_]+)/?(?:\\?.*|)$");

    private final SpotifyApi spotify;
    private final ScheduledExecutorService tokenUpdater;
    
    private final YoutubeAudioSourceManager yt;

    public Spotify(String clientId, String clientSecret) throws ParseException {
        yt = new YoutubeAudioSourceManager();

        spotify = SpotifyApi.builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        tokenUpdater = Executors.newSingleThreadScheduledExecutor();
        tokenUpdater.scheduleWithFixedDelay(() -> {
            try {
                this.spotify.setAccessToken(getAccessToken(clientId, clientSecret));
                PersyBotLogger.BOT_LOGGER.info("New spotify api token was set");
            } catch (Exception e) {
                PersyBotLogger.BOT_LOGGER.error(e);
                this.shutdown();
            }
        }, 0, 3500, TimeUnit.SECONDS);
    }

    private static String getAccessToken(String clientId, String clientSecret) {
        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://accounts.spotify.com/api/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("grant_type", "client_credentials")
                .field("client_id", clientId)
                .field("client_secret", clientSecret).asJson();

        return jsonResponse.getBody().getObject().getString("access_token");
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
                return loadPlaylistFromYT(manager, match.group(2));
            }

            match = TRACK_ID_PATTERN.matcher(url.toString());
            if (match.matches()) {
                return loadTrackFromYT(manager, match.group(2));
            }
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException | org.apache.hc.core5.http.ParseException | SpotifyWebApiException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
        }
        return null;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return false;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {

    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
        return null;
    }

    @Override
    public void shutdown() {
        this.tokenUpdater.shutdown();
    }

    private AudioItem loadPlaylistFromYT(AudioPlayerManager manager, String playlistId) throws org.apache.hc.core5.http.ParseException, SpotifyWebApiException, IOException {
        Playlist playlist = spotify.getPlaylist(playlistId).build().execute();

        if (playlist == null || playlist.getTracks().getTotal() == 0) return null;

        String[] trackIds = Arrays.stream(playlist.getTracks().getItems())
                .map(playlistTrack -> playlistTrack.getTrack().getId())
                .limit(50)
                .toArray(String[]::new);

        Track[] spotifyTracks = spotify.getSeveralTracks(trackIds).build().execute();

        List<AudioTrack> result = Arrays.stream(spotifyTracks).parallel().map(track -> loadFromYT(manager, track)).collect(Collectors.toList());

        return new BasicAudioPlaylist(playlist.getName(), result, null, false);
    }

    private AudioTrack loadTrackFromYT(AudioPlayerManager manager, String id) throws org.apache.hc.core5.http.ParseException, SpotifyWebApiException, IOException {
        Track spotifyTrack = spotify.getTrack(id).build().execute();
        return loadFromYT(manager, spotifyTrack);
    }

    private String collectArtists(ArtistSimplified[] artists) {
        return Arrays.stream(artists).map(ArtistSimplified::getName).collect(Collectors.joining(", "));
    }

    private AudioTrack loadFromYT(AudioPlayerManager manager, Track track) {
        String artists = collectArtists(track.getArtists());

        AudioReference reference = new AudioReference("ytsearch:" + artists + " - " + track.getName(), null);

        List<AudioTrack> tracks = ((BasicAudioPlaylist) this.yt.loadItem(manager, reference)).getTracks();

        if (tracks.size() > 0) {
            return tracks.get(0);
        }
        return null;
    }
}
