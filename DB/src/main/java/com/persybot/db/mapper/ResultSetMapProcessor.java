package com.persybot.db.mapper;

import com.persybot.db.DbData;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ResultSetMapProcessor {
    <T extends DbData> Map<Serializable, T> map(ResultSet data, Class<T> dataClass) throws SQLException;
    <T extends DbData> List<T> asList(ResultSet data, Class<T> dataClass) throws SQLException;

    <T extends DbData> ResultSetMapProcessor addMapper(ResultSetMapper<T> mapper, Class<T> dataClass);
}
