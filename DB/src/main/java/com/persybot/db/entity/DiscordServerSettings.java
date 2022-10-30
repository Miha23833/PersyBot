package com.persybot.db.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.jetbrains.annotations.Range;

@Entity
public class DiscordServerSettings implements DBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long settingsId;

    @Column(nullable = false)
    @Range(from = 0, to = 100)
    private byte volume;

    @Column(nullable = false, length = 2)
    private String prefix;

    @Column(columnDefinition = "TEXT")
    private String meetAudioLink;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "name")
    private EqualizerPreset preset;

    public DiscordServerSettings(byte volume, String prefix, EqualizerPreset preset) {
        this.volume = volume;
        this.prefix = prefix;
        this.preset = preset;
    }

    public DiscordServerSettings(byte volume, String prefix) {
        this.volume = volume;
        this.prefix = prefix;
        this.preset = null;
    }

    public DiscordServerSettings() {}

    public byte getVolume() {
        return volume;
    }
    public void setVolume(byte volume) {
        this.volume = volume;
    }

    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public long getSettingsId() {
        return settingsId;
    }

    public String getMeetAudioLink() {
        return meetAudioLink;
    }

    public void setMeetAudioLink(String meetAudioLink) {
        this.meetAudioLink = meetAudioLink;
    }

    public EqualizerPreset getPreset() {
        return preset;
    }
}
