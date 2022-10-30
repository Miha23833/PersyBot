package com.persybot.staticdata;

import com.persybot.db.entity.EqualizerPreset;
import com.persybot.service.Service;

import java.util.List;

public interface StaticData extends Service {
    EqualizerPreset getPreset(String presetName);

    List<String> getEqualizerPresetNames();

    void addPreset(EqualizerPreset preset);
}
