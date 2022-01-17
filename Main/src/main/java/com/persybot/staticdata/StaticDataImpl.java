package com.persybot.staticdata;

import com.persybot.db.entity.EqualizerPreset;
import com.persybot.staticdata.pojo.equalizer.EqualizerPresetContainer;
import com.persybot.staticdata.pojo.pagination.PageableMessages;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class StaticDataImpl implements StaticData {
    private static StaticDataImpl INSTANCE;
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final Map<Long, Long> guildsWithActiveVoiceChannel = new HashMap<>();
    private final PageableMessages pageableMessages = new PageableMessages();
    private EqualizerPresetContainer equalizerPresets = new EqualizerPresetContainer();

    @Override
    public Map<Long, Long> getGuildsWithActiveVoiceChannel() {
        return guildsWithActiveVoiceChannel;
    }

    @Override
    public PageableMessages getPageableMessages() {
        return this.pageableMessages;
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

    private StaticDataImpl(){
    }

    public static StaticDataImpl getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new StaticDataImpl();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }
}
