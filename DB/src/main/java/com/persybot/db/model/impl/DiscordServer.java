package com.persybot.db.model.impl;

import com.persybot.db.model.HbTable;
import org.hibernate.annotations.Table;

import javax.persistence.*;

@Entity
@Table(appliesTo = "DiscordServer")
public class DiscordServer implements HbTable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(updatable = false, nullable = false)
    private long serverId;

    @Column
    private long languageId;


    public void setDiscordServerSettings(DiscordServerSettings discordServerSettings) {
        this.discordServerSettings = discordServerSettings;
    }

    @OneToOne(mappedBy="discordServer")
    private DiscordServerSettings discordServerSettings;

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

    public DiscordServerSettings getDiscordServerSettings() {
        return discordServerSettings;
    }
}
