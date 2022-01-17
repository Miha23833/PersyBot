package com.persybot.db.mapper.impl;

import com.persybot.db.mapper.ResultSetRow;
import com.persybot.logger.impl.PersyBotLogger;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.Array;
import java.sql.SQLException;
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
        Object val = get(key);
        if (val == null) {
            return null;
        }
        return String.valueOf(val);
    }

    @Override
    public Boolean getBoolean(String key) {
        Object val = get(key);
        if (val == null) {
            return null;
        }
        return (boolean) val;
    }

    @Override
    public Byte getByte(String key) {
        Object val = get(key);
        if (val == null) {
            return null;
        }
        return (byte) val;
    }

    @Override
    public Short getShort(String key) {
        Object val = get(key);
        if (val == null) {
            return null;
        }
        return (short) val;
    }

    @Override
    public Integer getInt(String key) {
        Object val = get(key);
        if (val == null) {
            return null;
        }
        return (int) val;
    }

    @Override
    public Long getLong(String key) {
        Object val = get(key);
        if (val == null) {
            return null;
        }
        return (long) val;
    }

    @Override
    public Float getFloat(String key) {
        Object val = get(key);
        if (val == null) {
            return null;
        }
        return (float) val;
    }

    @Override
    public Double getDouble(String key) {
        Object val = get(key);
        if (val == null) {
            return null;
        }
        return (double) val;
    }

    @Override
    public float[] getFloatArr(String key) {
        Object val = get(key);
        if (val == null) {
            return null;
        }
        try {
            return ArrayUtils.toPrimitive((Float[]) (((Array) get(key)).getArray()));
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error("Got exception while trying to get float array.", e);
            throw new RuntimeException(e);
        }
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
