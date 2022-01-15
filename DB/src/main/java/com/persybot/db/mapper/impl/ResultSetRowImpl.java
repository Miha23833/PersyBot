package com.persybot.db.mapper.impl;

import com.persybot.db.mapper.ResultSetRow;

import java.util.HashMap;
import java.util.Map;

public class ResultSetRowImpl implements ResultSetRow {
    private final Map<String, Object> data;

    public ResultSetRowImpl() {
        this.data = new HashMap<>();
    }

    @Override
    public Object get(String key) {
        return data.get(key);
    }

    @Override
    public String getString(String key) {
        Object val = this.data.get(key);
        if (val == null) {
            return null;
        }
        return String.valueOf(val);
    }

    @Override
    public void add(String key, Object val) {
        this.data.put(key, val);
    }

    @Override
    public void fill(Map<String, Object> row) {
        this.data.putAll(row);
    }
}
