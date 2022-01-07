package com.persybot.staticdata;

import com.persybot.service.Service;
import com.persybot.staticdata.pojo.PageableMessages;

import java.util.Map;

public interface StaticData extends Service {
    Map<Long, Long> getGuildsWithActiveVoiceChannel();

    PageableMessages getPageableMessages();
}
