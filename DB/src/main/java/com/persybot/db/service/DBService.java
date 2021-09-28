package com.persybot.db.service;

import com.persybot.db.model.HbTable;
import com.persybot.masterslave.TaskMaster;
import com.persybot.service.Service;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface DBService extends Service, TaskMaster<HbTable> {
    <T extends HbTable> void add(T entity);
    <T extends HbTable> void delete(T entity);
    <T extends HbTable> void update(T entity);
    <T extends HbTable, Id extends Serializable> T get(Class<T> entityType, Id identifier) throws ExecutionException, InterruptedException, TimeoutException;
    <T extends HbTable, Id extends Serializable> T getOrInsertIfNotExists(Class<T> entityType, Id identifier, T entity) throws InterruptedException, ExecutionException, TimeoutException;
}
