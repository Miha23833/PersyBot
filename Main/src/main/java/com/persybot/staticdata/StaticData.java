package com.persybot.staticdata;

import com.persybot.service.Service;

import java.util.Map;

public interface StaticData extends Service {
    Map<Long, Long> getGuildsWithActiveVoiceChannel();
}
