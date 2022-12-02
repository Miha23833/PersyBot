package com.persybot.youtube.api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class YoutubeApiDataServiceImpl implements YoutubeApiDataService {
    private final YouTube api;

    @Override
    public YouTube getApi() {
        return this.api;
    }

    public YoutubeApiDataServiceImpl(String apiKey) throws GeneralSecurityException, IOException {
        this.api = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), new ApiKeyHttpRequestInitializer(apiKey))
                .setApplicationName("PersyBot")
                .build();
    }

    private record ApiKeyHttpRequestInitializer(String apiKey) implements HttpRequestInitializer, HttpExecuteInterceptor {
        @Override
        public void initialize(HttpRequest request) {
            request.setInterceptor(this);
        }

        @Override
        public void intercept(HttpRequest request) {
            request.getUrl().set("key", apiKey);
        }
    }
}
