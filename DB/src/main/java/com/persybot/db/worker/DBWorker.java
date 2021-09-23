package com.persybot.db.worker;

import com.persybot.db.model.HbTable;
import com.persybot.db.worker.impl.DbOperationResult;

public interface DBWorker {
    <T extends HbTable> void add(T entity);
    <T extends HbTable> void delete(T entity);
    <T extends HbTable> void update(T entity);
}
