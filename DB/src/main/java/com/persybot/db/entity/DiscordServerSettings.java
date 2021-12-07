package com.persybot.db.entity;

import com.persybot.db.DbData;
import com.persybot.db.sql.container.DiscordServerSettingsSqlContainer;

import java.io.Serializable;

public class DiscordServerSettings implements DbData {
    private Long serverId;

    private int volume = 100;

    private String prefix = "..";

    public DiscordServerSettings(Long serverId, int volume, String prefix) {
        this.serverId = serverId;
        this.volume = volume;
        this.prefix = prefix;
    }

    public DiscordServerSettings(Long serverId) {
        this.serverId = serverId;
    }

    public DiscordServerSettings() {

    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getServerId() {
        return serverId;
    }

    @Override
    public Serializable getIdentifier() {
        return serverId;
    }

    public static Class<DiscordServerSettingsSqlContainer> getSqlContainerClass() {
        return DiscordServerSettingsSqlContainer.class;
    }
}
