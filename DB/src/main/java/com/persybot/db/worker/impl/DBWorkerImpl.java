package com.persybot.db.worker.impl;

import com.persybot.db.common.OperationResult;
import com.persybot.db.model.HbTable;
import com.persybot.db.worker.DBWorker;
import com.persybot.logger.impl.PersyBotLogger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;

public class DBWorkerImpl implements DBWorker {
    private final SessionFactory sessionFactory;

    public DBWorkerImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <T extends HbTable> void add(T entity) {
        Session dbSession = sessionFactory.openSession();
        Transaction transaction = dbSession.beginTransaction();

        OperationResult constraintCheckResult = entity.validate();
        if (constraintCheckResult.isValid()) {
            try {
                dbSession.save(entity);
                transaction.commit();
            } catch (Exception e) {
                PersyBotLogger.BOT_LOGGER.error(e);
                transaction.rollback();
            }
        } else {
            PersyBotLogger.BOT_LOGGER.error("Fail to add " + entity.getClass() + ": " + constraintCheckResult.getFailDescription());
        }
    }

    @Override
    public <T extends HbTable> void delete(T entity) {
        Session dbSession = sessionFactory.openSession();
        Transaction transaction = dbSession.beginTransaction();
        try {
            dbSession.save(entity);
            transaction.commit();
        } catch (Exception e) {
            PersyBotLogger.BOT_LOGGER.error(e);
            transaction.rollback();
        }
    }

    @Override
    public <T extends HbTable> void update(T entity) {
        Session dbSession = sessionFactory.openSession();
        Transaction transaction = dbSession.beginTransaction();

        OperationResult constraintCheckResult = entity.validate();
        if (constraintCheckResult.isValid()) {
            try {
                dbSession.update(entity);
                transaction.commit();
            } catch (Exception e) {
                PersyBotLogger.BOT_LOGGER.error(e);
                transaction.rollback();
            }
        } else {
            PersyBotLogger.BOT_LOGGER.error("Fail to add " + entity.getClass() + ": " + constraintCheckResult.getFailDescription());
        }
    }
}
