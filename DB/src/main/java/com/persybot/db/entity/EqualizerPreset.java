package com.persybot.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EqualizerPreset implements DBEntity{
    @Id
    private String name;

    @Column(nullable = false, columnDefinition = "real[]")
    private float[] bands;

    public EqualizerPreset(String name, float[] bands) {
        this.name = name;
        this.bands = bands;
    }

    public EqualizerPreset() {}
    public String getName() {
        return name;
    }

    public float[] getBands() {
        return bands;
    }
    public void setBands(float[] bands) {
        this.bands = bands;
    }
}
