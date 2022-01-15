package com.persybot.db.entity.mappers;

import com.persybot.db.entity.ServerAudioSettings;
import com.persybot.db.mapper.ResultSetMapper;
import com.persybot.db.mapper.ResultSetRow;

public class ServerAudioSettingsMapper implements ResultSetMapper<ServerAudioSettings> {
    @Override
    public ServerAudioSettings map(ResultSetRow dataRow) {
        ServerAudioSettings settings = new ServerAudioSettings((long) dataRow.get("serverid"));
        settings.setMeetAudioLink(String.valueOf(dataRow.get("meetaudiolink")));

        return settings;
    }
}
