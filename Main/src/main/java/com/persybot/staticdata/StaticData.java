package com.persybot.staticdata;

import com.persybot.db.entity.EqualizerPreset;
import com.persybot.service.Service;
import com.persybot.staticdata.pojo.pagination.PageableMessages;

import java.util.List;
import java.util.Map;

public interface StaticData extends Service {
    Map<Long, Long> getGuildsWithActiveVoiceChannel();

    PageableMessages getPageableMessages();

    EqualizerPreset getPreset(String presetName);

    List<String> getEqualizerPresetNames();

    void addPreset(EqualizerPreset preset);
}
