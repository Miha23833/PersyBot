package com.persybot.db.model.impl;

import com.persybot.db.model.HbTable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Table(appliesTo = "DiscordServer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DiscordServer implements HbTable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(updatable = false, nullable = false)
    private long serverId;

    @Column
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
}
