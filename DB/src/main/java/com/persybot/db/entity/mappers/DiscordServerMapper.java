package com.persybot.db.entity.mappers;

import com.persybot.db.entity.DiscordServer;
import com.persybot.db.mapper.ResultSetMapper;
import com.persybot.db.mapper.ResultSetRow;

public class DiscordServerMapper implements ResultSetMapper<DiscordServer> {
    @Override
    public DiscordServer map(ResultSetRow dataRow) {
        DiscordServer ds = new DiscordServer();

        ds.setLanguageId((long) dataRow.get("languageid"));
        ds.setServerId((long) dataRow.get("serverid"));

        return ds;
    }
}
