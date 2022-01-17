package com.persybot.db.mapper.impl;

import com.persybot.db.DbData;
import com.persybot.db.mapper.ResultSetMapProcessor;
import com.persybot.db.mapper.ResultSetMapper;
import com.persybot.db.mapper.ResultSetRow;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultSetMapProcessorImpl implements ResultSetMapProcessor {
    private final Map<Class<? extends DbData>, ResultSetMapper<? extends DbData>> mappers;

    public ResultSetMapProcessorImpl() {
        mappers = new HashMap<>();
    }

    @Override
    public <T extends DbData> Map<Long, T> map(ResultSet data, Class<T> dataClass) throws SQLException {
        if (!mappers.containsKey(dataClass)) {
            throw new IllegalArgumentException("Cannot map a result set to " + dataClass);
        }

        List<ResultSetRow> rows = toRowList(data);
        ResultSetMapper<?> mapper = mappers.get(dataClass);

        Map<Long, T> result = new HashMap<>();

        rows.stream().map(mapper::map).map(dataClass::cast).forEach(val -> result.put(val.getIdentifier(), val));

        return result;
    }

    @Override
    public <T extends DbData> List<T> asList(ResultSet data, Class<T> dataClass) throws SQLException {
        if (!mappers.containsKey(dataClass)) {
            throw new IllegalArgumentException("Cannot map a result set to " + dataClass);
        }

        List<ResultSetRow> rows = toRowList(data);
        ResultSetMapper<?> mapper = mappers.get(dataClass);

        return rows.stream().map((ResultSetRow dataRow) -> dataClass.cast(mapper.map(dataRow))).collect(Collectors.toList());
    }

    @Override
    public <T extends DbData> T getSingle(ResultSet data, Class<T> dataClass) throws SQLException {
        if (!mappers.containsKey(dataClass)) {
            throw new IllegalArgumentException("Cannot map a result set to " + dataClass);
        }

        ResultSetMetaData metaData = data.getMetaData();
        int columnCount = metaData.getColumnCount();
        ResultSetMapper<?> mapper = mappers.get(dataClass);

        if (!data.next()) {
            return null;
        }

        ResultSetRow row = new ResultSetRowImpl();
        for (int i = 1; i <= columnCount; i++) {
            row.add(metaData.getColumnName(i), data.getObject(i));
        }
        if (data.next()) {
            throw new IllegalArgumentException("Data with more than 1 row is not allowed");
        }
        return dataClass.cast(mapper.map(row));
    }

    @Override
    public <T extends DbData> ResultSetMapProcessor addMapper(Class<T> entity, ResultSetMapper<T> mapper) {
        this.mappers.put(entity, mapper);
        return this;
    }

    private List<ResultSetRow> toRowList(ResultSet data) throws SQLException {
        List<ResultSetRow> rows = new LinkedList<>();
        ResultSetMetaData metaData = data.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (data.next()) {
            ResultSetRow row = new ResultSetRowImpl();
            for (int i = 1; i <= columnCount; i++) {
                row.add(metaData.getColumnName(i), data.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }

    private <T extends DbData> ResultSetMapper<?> getMapper(Class<T> dataClass) {
        return this.mappers.get(dataClass);
    }
}
