package com.persybot.db.entity.mappers;

import com.persybot.db.entity.EqualizerPreset;
import com.persybot.db.mapper.ResultSetMapper;
import com.persybot.db.mapper.ResultSetRow;

public class EqualizerPresetMapper implements ResultSetMapper<EqualizerPreset> {
    @Override
    public EqualizerPreset map(ResultSetRow dataRow) {
        return new EqualizerPreset(dataRow.getLong("id"), dataRow.getString("name"), dataRow.getFloatArr("bands"));
    }
}
