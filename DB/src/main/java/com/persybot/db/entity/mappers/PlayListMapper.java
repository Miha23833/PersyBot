package com.persybot.db.entity.mappers;

import com.persybot.db.entity.PlayList;
import com.persybot.db.mapper.ResultSetMapper;
import com.persybot.db.mapper.ResultSetRow;

public class PlayListMapper implements ResultSetMapper<PlayList> {
    @Override
    public PlayList map(ResultSetRow dataRow) {
        return new PlayList((long) dataRow.get("id"), (long) dataRow.get("serverid"), dataRow.getString("name"), dataRow.getString("url"));
    }
}
