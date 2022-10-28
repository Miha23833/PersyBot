package com.persybot.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class EqualizerPreset implements DBEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long presetId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "real[]")
    private float[] bands;

    public EqualizerPreset(Long presetId, String name, float[] bands) {
        this.presetId = presetId;
        this.name = name;
        this.bands = bands;
    }

    public EqualizerPreset() {}

    public Long getPresetId() {
        return presetId;
    }
    public String getName() {
        return name;
    }

    public float[] getBands() {
        return bands;
    }
    public void setBands(float[] bands) {
        this.bands = bands;
    }

    @Override
    public long getId() {
        return Objects.requireNonNull(presetId);
    }
}
