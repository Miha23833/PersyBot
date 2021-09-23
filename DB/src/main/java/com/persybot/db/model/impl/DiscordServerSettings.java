package com.persybot.db.model.impl;

import com.persybot.db.common.OperationResult;
import com.persybot.db.common.constraint.impl.ValidateConstraintResult;
import com.persybot.db.model.HbTable;
import org.hibernate.annotations.Table;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Table(appliesTo = "ServerSettings")
public class DiscordServerSettings implements HbTable {
    @Column
    @OneToOne
    @JoinColumn(name = "discordServerId")
    private DiscordServer discordServerId;

    @Column(nullable = false)
    private int volume = 100;

    @Column(length = 2)
    private String prefix;

    public DiscordServer getDiscordServerId() {
        return discordServerId;
    }

    public void setDiscordServerId(DiscordServer discordServerId) {
        this.discordServerId = discordServerId;
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
}
