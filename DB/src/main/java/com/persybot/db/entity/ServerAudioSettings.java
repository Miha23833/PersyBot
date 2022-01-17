package com.persybot.db.entity;

import com.persybot.db.DbData;

public class ServerAudioSettings implements DbData {
    private final Long serverId;
    private String meetAudioLink;
    private Long equalizerPresetId;

    public ServerAudioSettings(Long serverId) {this.serverId = serverId;}

    public ServerAudioSettings(Long serverId, String meetAudioLink, Long equalizerPresetId) {
        this(serverId);
        this.meetAudioLink = meetAudioLink;
        this.equalizerPresetId = equalizerPresetId;
    }

    public Long getEqualizerPresetId() {
        return equalizerPresetId;
    }

    public void setMeetAudioLink(String link) {
        this.meetAudioLink = link;
    }

    public String getMeetAudioLink() {
        return this.meetAudioLink;
    }

    @Override
    public Long getIdentifier() {
        return this.serverId;
    }
}
