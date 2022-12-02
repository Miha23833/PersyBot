package com.persybot.youtube.api;

import com.google.api.services.youtube.YouTube;
import com.persybot.service.Service;

public interface YoutubeApiDataService extends Service {
    YouTube getApi();
}
