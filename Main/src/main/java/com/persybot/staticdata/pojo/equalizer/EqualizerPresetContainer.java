package com.persybot.staticdata.pojo.equalizer;

import com.persybot.db.entity.EqualizerPreset;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EqualizerPresetContainer {
    private final Map<String, EqualizerPreset> presets;

    public EqualizerPresetContainer() {
        this.presets = new HashMap<>();
    }

    @Nullable
    public EqualizerPreset getPreset(String name) {
        return presets.getOrDefault(name, null);
    }

    public List<EqualizerPreset> getAll() {
        return new ArrayList<>(this.presets.values());
    }

    public void add(EqualizerPreset preset) {
        this.presets.put(preset.getName(), preset);
    }
}
