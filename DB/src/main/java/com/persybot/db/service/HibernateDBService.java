package com.persybot.db.service;

import com.persybot.db.entity.DBEntity;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.entity.EqualizerPreset;
import com.persybot.db.entity.PlayList;
import com.persybot.db.hibernate.dao.DAO;
import com.persybot.db.hibernate.dao.DiscordServerDAO;
import com.persybot.db.hibernate.dao.EqualizerPresetDAO;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class HibernateDBService implements DBService {

    private final Map<Class<? extends DBEntity>, DAO<? extends DBEntity>> dataAccessObjects = new HashMap<>();

    public HibernateDBService(Properties dbProperties) {
        SessionFactory sessionFactory = new MetadataSources(new StandardServiceRegistryBuilder()
                .loadProperties(new File(dbProperties.getProperty("HIBERNATE_CONFIG_PATH")))
                .build())
                .addAnnotatedClass(DiscordServer.class)
                .addAnnotatedClass(DiscordServerSettings.class)
                .addAnnotatedClass(EqualizerPreset.class)
                .addAnnotatedClass(PlayList.class)
                .buildMetadata().buildSessionFactory();

        populateDAO(sessionFactory);
    }

    private void populateDAO(SessionFactory sessionFactory) {
        dataAccessObjects.put(DiscordServer.class, new DiscordServerDAO(sessionFactory));
        dataAccessObjects.put(EqualizerPreset.class, new EqualizerPresetDAO(sessionFactory));
    }

    @Override
    public <T extends DBEntity> Optional<T> create(T entity) {
        return Optional.ofNullable((T) dataAccessObjects.get(entity.getClass()).create(entity));
    }

    @Override
    public <T extends DBEntity> Optional<T> read(long id, Class<T> dataClass) {
        return Optional.ofNullable((T) dataAccessObjects.get(dataClass).read(id));
    }

    @Override
    public <T extends DBEntity> Optional<T> update(T entity) {
        return Optional.ofNullable((T) dataAccessObjects.get(entity.getClass()).update(entity));
    }

    @Override
    public void delete(DBEntity entity) {
        dataAccessObjects.get(entity.getClass()).delete(entity);
    }

    @Override
    public <T extends DBEntity> Optional<List<T>> readAll(Class<T> dataClass) {
        return Optional.ofNullable(dataAccessObjects.get(dataClass).readAll().stream().map(dataClass::cast).collect(Collectors.toList()));
    }
}
