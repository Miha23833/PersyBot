package com.persybot.spotify.api;

import com.persybot.logger.impl.PersyBotLogger;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpotifyApiDataServiceImpl implements SpotifyApiDataService {
    private final SpotifyApi spotifyApi;
    private final ScheduledExecutorService tokenUpdater;

    public SpotifyApiDataServiceImpl(String clientId, String clientSecret) {
        spotifyApi = SpotifyApi.builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        tokenUpdater = Executors.newSingleThreadScheduledExecutor();
        tokenUpdater.scheduleWithFixedDelay(() -> {
            try {
                this.spotifyApi.setAccessToken(getAccessToken(clientId, clientSecret));
                PersyBotLogger.BOT_LOGGER.info("New spotify api token was set");
            } catch (Exception e) {
                PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }, 0, 59, TimeUnit.MINUTES);
    }

    @Override
    public SpotifyApi getApi() {
        return this.spotifyApi;
    }

    private static String getAccessToken(String clientId, String clientSecret) {
        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://accounts.spotify.com/api/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("grant_type", "client_credentials")
                .field("client_id", clientId)
                .field("client_secret", clientSecret).asJson();

        return jsonResponse.getBody().getObject().getString("access_token");
    }
}
