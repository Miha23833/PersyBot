package com.persybot.db.service;

import com.persybot.db.model.HbTable;
import com.persybot.db.worker.impl.DBWorkerImpl;
import com.persybot.logger.impl.PersyBotLogger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DBServiceImpl implements DBService {
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static volatile DBServiceImpl INSTANCE;
    protected final int countOfWorkers;
    protected final BlockingQueue<RunnableFuture<HbTable>> tasks;
    protected final List<DBWorkerImpl> workers;
    private final SessionFactory sessionFactory;
    protected AtomicBoolean onPause = new AtomicBoolean(true);

    private DBServiceImpl(int countOfWorkers) {
        this.countOfWorkers = countOfWorkers;

        Configuration configuration = new Configuration();
        Properties properties = new Properties();

        loadProperties(properties, "DB\\src\\main\\resources\\hibernate.cfg.xml");

        configuration.addProperties(properties);
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();

        this.tasks = new LinkedBlockingDeque<>();

        workers = createWorkers();
    }

    public static DBServiceImpl getInstance(int countOfWorkers) {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new DBServiceImpl(countOfWorkers);
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    @Override
    public void addTask(RunnableFuture<HbTable> task) {
        tasks.add(task);
    }

    @Override
    public void start() {
        onPause.set(false);
        workers.forEach(Thread::start);
    }

    @Override
    public void stop() {
        onPause.set(true);
    }

    @Override
    public <T extends HbTable> void add(T entity) {
        addTask(new FutureTask<>(() -> {
            Session dbSession = sessionFactory.openSession();
            Transaction transaction = dbSession.beginTransaction();
                try {
                    dbSession.save(entity);
                    transaction.commit();
                } catch (Exception e) {
                    PersyBotLogger.BOT_LOGGER.error(e);
                    transaction.rollback();
                } finally {
                    if (dbSession.isOpen()) {
                        dbSession.close();
                    }
                }
            return null;
        }));
    }

    @Override
    public <T extends HbTable> void delete(T entity) {
        addTask(new FutureTask<>(() -> {
            Session dbSession = sessionFactory.openSession();
            Transaction transaction = dbSession.beginTransaction();
            try {
                dbSession.delete(entity);
                transaction.commit();
            } catch (Exception e) {
                PersyBotLogger.BOT_LOGGER.error(e);
                transaction.rollback();
            } finally {
                if (dbSession.isOpen()) {
                    dbSession.close();
                }
            }
            return null;
        }));
    }

    @Override
    public <T extends HbTable> void update(T entity) {
        addTask(new FutureTask<>(() -> {
            Session dbSession = sessionFactory.openSession();
            Transaction transaction = dbSession.beginTransaction();
                try {
                    dbSession.update(entity);
                    transaction.commit();
                } catch (Exception e) {
                    PersyBotLogger.BOT_LOGGER.error(e);
                } finally {
                    if (dbSession.isOpen()) {
                        dbSession.close();
                    }
                }
            return null;
        }));
    }

    @Override
    public <T extends HbTable, I extends Serializable> T get(Class<T> entityType, I identifier) throws ExecutionException, InterruptedException {
        RunnableFuture<HbTable> task = new FutureTask<>(() -> {
            Session dbSession = sessionFactory.openSession();
            Transaction transaction = dbSession.beginTransaction();
            T entity = null;
            try {
                entity = dbSession.get(entityType, identifier);
                transaction.commit();
            } catch (Exception e) {
                PersyBotLogger.BOT_LOGGER.error(e);
            } finally {
                if (dbSession.isOpen()) {
                    dbSession.close();
                }
            }
            return entity;
        });
        addTask(task);
        return entityType.cast(task.get());
    }

    private void loadProperties(Properties properties, String path) {
        try (FileInputStream fis = new FileInputStream(new File(path))) {
            properties.load(fis);
        } catch (IOException e) {
            PersyBotLogger.BOT_LOGGER.fatal("Cannot read hibernate properties file:\n", e);
        }
    }

    private List<DBWorkerImpl> createWorkers() {
        List<DBWorkerImpl> workers = new ArrayList<>();
        for (int i = 0; i < countOfWorkers; i++) {
            workers.add(new DBWorkerImpl(sessionFactory, tasks, onPause));
        }
        return workers;
    }

}
