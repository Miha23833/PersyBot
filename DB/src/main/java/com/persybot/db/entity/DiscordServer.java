package com.persybot.db.entity;

import com.persybot.db.DbData;
import com.persybot.db.sql.container.DiscordServerSqlContainer;

import java.io.Serializable;

public class DiscordServer implements DbData {

    private long serverId;
    private long languageId;

    public DiscordServer(long serverId, long languageId) {
        this.serverId = serverId;
        this.languageId = languageId;
    }

    public DiscordServer() {

    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }

    @Override
    public Serializable getIdentifier() {
        return this.getServerId();
    }

    public static Class<DiscordServerSqlContainer> getSqlContainerClass() {
        return DiscordServerSqlContainer.class;
    }
}
