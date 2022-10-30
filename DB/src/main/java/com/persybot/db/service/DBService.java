package com.persybot.db.service;

import com.persybot.db.entity.DBEntity;
import com.persybot.service.Service;

import java.util.List;
import java.util.Optional;

public interface DBService extends Service {
    <T extends DBEntity> Optional<T> create(T entity);
    <T extends DBEntity> Optional<T> read(long id, Class<T> dataClass);
    <T extends DBEntity> T readAssured(long id, Class<T> dataClass);
    <T extends DBEntity> Optional<T> update(T entity);
    void delete(DBEntity entity);

    <T extends DBEntity> Optional<List<T>> readAll(Class<T> dataClass);
}
