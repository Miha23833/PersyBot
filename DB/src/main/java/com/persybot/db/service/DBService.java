package com.persybot.db.service;

import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.service.Service;

public interface DBService extends Service {
    DiscordServerSettings getDiscordServerSettings(long id);
    void updateDiscordServerSettings(DiscordServerSettings entity);

    DiscordServer getDiscordServer(long id);
    void updateDiscordServer(DiscordServer entity);
}
