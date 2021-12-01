package com.persybot.db.entity.mappers;

import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.mapper.ResultSetMapper;
import com.persybot.db.mapper.ResultSetRow;

public class DiscordServerSettingsMapper implements ResultSetMapper<DiscordServerSettings> {
    @Override
    public DiscordServerSettings map(ResultSetRow dataRow) {
        DiscordServerSettings entity = new DiscordServerSettings((long) dataRow.get("serverid"));

        entity.setPrefix(String.valueOf(dataRow.get("prefix")));
        entity.setVolume((int) dataRow.get("volume"));

        return entity;
    }
}
