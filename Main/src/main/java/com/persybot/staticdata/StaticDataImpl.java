package com.persybot.staticdata;

import com.persybot.db.entity.EqualizerPreset;
import com.persybot.staticdata.pojo.equalizer.EqualizerPresetContainer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StaticDataImpl implements StaticData {
    private final Map<Long, Long> guildsWithActiveVoiceChannel = new HashMap<>();
    private EqualizerPresetContainer equalizerPresets = new EqualizerPresetContainer();

    @Override
    public Map<Long, Long> getGuildsWithActiveVoiceChannel() {
        return guildsWithActiveVoiceChannel;
    }

    @Nullable
    @Override
    public EqualizerPreset getPreset(String presetName) {
        return this.equalizerPresets.getPreset(presetName);
    }

    @Override
    public List<String> getEqualizerPresetNames() {
        return this.equalizerPresets.getAll().stream().map(EqualizerPreset::getName).collect(Collectors.toList());
    }

    @Override
    public void addPreset(EqualizerPreset preset) {
        this.equalizerPresets.add(preset);
    }

    public StaticDataImpl(){
    }
}
