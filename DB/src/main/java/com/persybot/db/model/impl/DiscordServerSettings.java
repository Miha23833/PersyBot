package com.persybot.db.model.impl;

import com.persybot.db.common.OperationResult;
import com.persybot.db.common.constraint.impl.ValidateConstraintResult;
import com.persybot.db.model.HbTable;
import org.hibernate.annotations.Table;

import javax.persistence.*;

@Entity
@Table(appliesTo = "DiscordServerSettings")
public class DiscordServerSettings implements HbTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "serverId", updatable = false)
    private Long serverId;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="discordServer")
    private DiscordServer discordServer;

    @Column(nullable = false)
    private int volume = 100;

    @Column(length = 2)
    private String prefix;

    public DiscordServerSettings(Long serverId, DiscordServer discordServer, int volume, String prefix) {
        this.serverId = serverId;
        this.discordServer = discordServer;
        this.volume = volume;
        this.prefix = prefix;
    }

    public DiscordServerSettings() {

    }

    public DiscordServer getDiscordServer() {
        return discordServer;
    }

    public void setDiscordServer(DiscordServer discordServer) {
        this.discordServer = discordServer;
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

    @Override
    public OperationResult validate() {
        OperationResult result = ValidateConstraintResult.getDefaultValid();

        if (volume < 0 || volume > 100) {
            result.addInvalidCause("Invalid volume value: " + volume);
        }

        if (prefix == null) {
            result.addInvalidCause("Prefix cannot be null");
        } else if (prefix.length() > 2) {
            result.addInvalidCause("Prefix length cannot be more than 2");
        }
        return result;
    }

    public Long getServerId() {
        return serverId;
    }
}
