package com.persybot.db.worker.impl;

import com.persybot.db.model.HbTable;
import com.persybot.masterslave.impl.AbstractWorker;
import org.hibernate.SessionFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class DBWorkerImpl extends AbstractWorker<HbTable> {
    private final SessionFactory sessionFactory;

    public DBWorkerImpl(SessionFactory sessionFactory, BlockingQueue<RunnableFuture<HbTable>> tasks, AtomicBoolean isMasterOnPause) {
        super(tasks, isMasterOnPause);
        this.sessionFactory = sessionFactory;
    }
}
