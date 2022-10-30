package com.persybot.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EqualizerPreset implements DBEntity{
    private static final int BANDS_COUNT = 15;

    @Id
    private String name;

    @Column(nullable = false, columnDefinition = "real[]")
    private float[] bands;

    public EqualizerPreset(String name, Integer[] bands) {
        if (bands.length != BANDS_COUNT) {
            throw new IllegalArgumentException("Bands length is " + bands.length + ", but must be " + BANDS_COUNT);
        }

        this.name = name;
        this.bands = convertBands(bands);
    }

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

    private float[] convertBands(Integer[] bands) {
        float[] result = new float[BANDS_COUNT];

        for (int i = 0; i < BANDS_COUNT; i++) {
            if (bands[i] >= 0) {
                result[i] = Math.min((float) (bands[i] * 0.1), 1);
            } else {
                result[i] = Math.max(((float) (-0.025) * bands[i]), -0.25f);
            }
        }
        return result;
    }
}
