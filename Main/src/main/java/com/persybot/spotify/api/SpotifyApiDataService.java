package com.persybot.spotify.api;

import com.persybot.service.Service;
import se.michaelthelin.spotify.SpotifyApi;

public interface SpotifyApiDataService extends Service {
    SpotifyApi getApi();
}
