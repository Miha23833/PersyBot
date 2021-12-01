package com.persybot.db.service;

import com.persybot.db.DbData;
import com.persybot.masterslave.TaskMaster;
import com.persybot.service.Service;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface DBService extends Service, TaskMaster<DbData> {
    <T extends DbData> void add(T entity);
    <T extends DbData> void delete(T entity);
    <T extends DbData> void update(T entity);
    <T extends DbData, Id extends Serializable> T get(Class<T> entityType, Id identifier) throws ExecutionException, InterruptedException, TimeoutException;
    <T extends DbData, Id extends Serializable> T getOrInsertIfNotExists(Class<T> entityType, Id identifier, T entity) throws InterruptedException, ExecutionException, TimeoutException;
}
