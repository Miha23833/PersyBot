package com.persybot.db.mapper;

import java.util.Map;

public interface ResultSetRow {
    Object get(String key);

    void add(String key, Object val);
    void fill(Map<String, Object> row);
}
