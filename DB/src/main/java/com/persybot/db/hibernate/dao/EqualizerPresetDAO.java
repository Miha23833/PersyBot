package com.persybot.db.hibernate.dao;

import com.persybot.db.entity.EqualizerPreset;
import org.hibernate.SessionFactory;

public class EqualizerPresetDAO extends DAO<EqualizerPreset> {
    public EqualizerPresetDAO(SessionFactory sessionFactory) {
        super(sessionFactory, EqualizerPreset.class);
    }
}
