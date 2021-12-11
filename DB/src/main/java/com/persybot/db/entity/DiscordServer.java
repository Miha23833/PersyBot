package com.persybot.db.entity;

import com.persybot.db.DbData;
import com.persybot.db.sql.container.DiscordServerSqlContainer;

public class DiscordServer implements DbData {

    private long serverId;
    private int languageId;

    public DiscordServer(long serverId, int languageId) {
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

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    @Override
    public Long getIdentifier() {
        return this.getServerId();
    }

    public static Class<DiscordServerSqlContainer> getSqlContainerClass() {
        return DiscordServerSqlContainer.class;
    }
}
