package com.persybot.db.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashMap;
import java.util.Map;

@Entity
public class DiscordServer implements DBEntity {
    @Id
    private Long guildId;

    @Column(nullable = false)
    private Integer languageId;

    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "server_settings_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DiscordServerSettings settings;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "play_list_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @MapKey(name = "name")
    private Map<String, PlayList> playLists = new HashMap<>();

    public DiscordServer(long guildId, int languageId, DiscordServerSettings settings, Map<String, PlayList> playLists) {
        this.guildId = guildId;
        this.languageId = languageId;
        this.settings = settings;
        this.playLists = playLists;
    }

    public DiscordServer(long guildId, int languageId, DiscordServerSettings settings) {
        this.guildId = guildId;
        this.languageId = languageId;
        this.settings = settings;
    }

    public DiscordServer() {}

    public long getGuildId() {
        return guildId;
    }
    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public long getLanguageId() {
        return languageId;
    }
    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public DiscordServerSettings getSettings() {
        return settings;
    }

    public Map<String, PlayList> getPlayLists() {
        return playLists;
    }
}
