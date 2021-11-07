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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

    private DBServiceImpl() {
        Configuration configuration = new Configuration();
        Properties properties = new Properties();

        loadProperties(properties, "DB\\src\\main\\resources\\hibernate.cfg.xml");

        configuration.addProperties(properties);
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();

        this.countOfWorkers = Integer.parseInt(properties.getProperty("db.workers.count", "7"));
        this.tasks = new LinkedBlockingDeque<>();

        workers = createWorkers();
    }

    private DBServiceImpl(Properties properties) {
        Configuration configuration = new Configuration();
        configuration.addProperties(properties);

        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();

        this.tasks = new LinkedBlockingQueue<>();

        countOfWorkers = Integer.parseInt(properties.getProperty("db.workers.count", "7"));
        workers = createWorkers();
    }

    public static DBServiceImpl getInstance(Properties properties) {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new DBServiceImpl(properties);
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
    public <T extends HbTable, Id extends Serializable> T get(Class<T> entityType, Id identifier) throws InterruptedException, ExecutionException, TimeoutException {
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

        return entityType.cast(task.get(2000, TimeUnit.MILLISECONDS));
    }

    @Override
    public <T extends HbTable, Id extends Serializable> T getOrInsertIfNotExists(Class<T> entityType, Id identifier, T entity) throws InterruptedException, ExecutionException, TimeoutException {
        RunnableFuture<HbTable> task = new FutureTask<>(() -> {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            T result;
            try {
                result = session.get(entityType, identifier);
                if (result == null) {
                    session.save(entity);
                    result = entity;
                }
                transaction.commit();
            } finally {
                if (session.isOpen()) {
                    session.close();
                }
            }
            return result;
        });
        addTask(task);

        return entityType.cast(task.get(2000, TimeUnit.MILLISECONDS));
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
