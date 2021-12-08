package com.persybot.db.entity;

import com.persybot.db.DbData;

public class PlayList implements DbData {

    public PlayList(Long serverId, String name, String url) {
        this.serverId = serverId;
        this.name = name;
        this.url = url;
    }

    public PlayList(Long id, Long serverId, String name, String url) {
        this.id = id;
        this.serverId = serverId;
        this.name = name;
        this.url = url;
    }

    private Long serverId;
    private Long id;
    private String name;
    private String url;

    @Override
    public Long getIdentifier() {
        return this.id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
