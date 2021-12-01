package com.persybot.db.mapper;

import com.persybot.db.DbData;

public interface ResultSetMapper<T extends DbData> {
    T map(ResultSetRow dataRow);
}
