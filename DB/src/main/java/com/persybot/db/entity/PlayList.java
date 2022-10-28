package com.persybot.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class PlayList implements DBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long playListId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    public PlayList(Long playListId, String name, String url) {
        this.playListId = playListId;
        this.name = name;
        this.url = url;
    }

    public PlayList() {}

    public Long getPlayListId() {
        return playListId;
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

    @Override
    public long getId() {
        return Objects.requireNonNull(playListId);
    }
}
