package com.persybot.staticdata;

import java.util.HashMap;
import java.util.Map;

public class StaticDataImpl implements StaticData {
    private final Map<Long, Long> guildsWithActiveVoiceChannel = new HashMap<>();

    @Override
    public Map<Long, Long> getGuildsWithActiveVoiceChannel() {
        return guildsWithActiveVoiceChannel;
    }

    public StaticDataImpl(){
    }
}
