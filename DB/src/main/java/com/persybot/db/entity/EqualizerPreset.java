package com.persybot.db.entity;

import com.persybot.db.DbData;

public class EqualizerPreset implements DbData {
    private Long id;
    private String name;
    private float[] bands;

    public EqualizerPreset(Long id, String name, float[] bands) {
        this.id = id;
        this.name = name;
        this.bands = bands;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float[] getBands() {
        return bands;
    }

    public void setBands(float[] bands) {
        this.bands = bands;
    }
}
