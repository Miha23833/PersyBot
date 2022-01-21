package com.persybot.db.entity;

import com.persybot.db.DbData;

public class DiscordServerSettings implements DbData {
    private final Long serverId;

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
    public Long getIdentifier() {
        return serverId;
    }
}
