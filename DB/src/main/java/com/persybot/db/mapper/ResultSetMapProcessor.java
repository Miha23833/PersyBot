package com.persybot.db.mapper;

import com.persybot.db.DbData;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ResultSetMapProcessor {
    <T extends DbData> Map<Long, T> map(ResultSet data, Class<T> dataClass) throws SQLException;
    <T extends DbData> List<T> asList(ResultSet data, Class<T> dataClass) throws SQLException;
    <T extends DbData> T getSingle(ResultSet data, Class<T> dataClass) throws SQLException;

    default boolean getSingleBoolean(ResultSet data) throws SQLException {
        ResultSetMetaData metaData = data.getMetaData();
        int columnCount = metaData.getColumnCount();

        if (!data.next()) {
            throw new IllegalArgumentException("Data does not contain rows");
        }

        if (!data.isLast()) {
            throw new IllegalArgumentException("Data contains more than 1 row");
        }

        if (columnCount != 1) {
            throw new IllegalArgumentException("Data contains more than 1 column");
        }

        if (metaData.getColumnType(1) != Types.BOOLEAN && metaData.getColumnType(1) != Types.BIT ) {
            throw new IllegalArgumentException("Column is not boolean");
        }

        return data.getBoolean(1);
    }

    default Long getSingleLong(ResultSet data) throws SQLException {
        ResultSetMetaData metaData = data.getMetaData();
        int columnCount = metaData.getColumnCount();

        if (!data.next()) {
            throw new IllegalArgumentException("Data does not contain rows");
        }

        if (!data.isLast()) {
            throw new IllegalArgumentException("Data contains more than 1 row");
        }

        if (columnCount != 1) {
            throw new IllegalArgumentException("Data contains more than 1 column");
        }

        Set<Integer> allowedDataTypes = Set.of(
                Types.TINYINT,
                Types.SMALLINT,
                Types.INTEGER,
                Types.BIGINT);

        if (!allowedDataTypes.contains(metaData.getColumnType(1))) {
            throw new IllegalArgumentException("Column is not numeric");
        }

        return (long) data.getObject(1);
    }

    <T extends DbData> ResultSetMapProcessor addMapper(ResultSetMapper<T> mapper, Class<T> dataClass);

}
