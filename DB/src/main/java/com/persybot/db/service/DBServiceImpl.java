package com.persybot.db.service;

import com.persybot.db.DbData;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.mapper.DiscordServerSettingsMapper;
import com.persybot.db.mapper.ResultSetMapProcessor;
import com.persybot.db.mapper.impl.ResultSetMapProcessorImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//import com.persybot.db.entity.DiscordServer;
//import com.persybot.db.entity.DiscordServerSettings;

public class DBServiceImpl /* implements DBService */ {
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static volatile DBServiceImpl INSTANCE;
//    protected final int countOfWorkers;
    protected final BlockingQueue<RunnableFuture<DbData>> tasks;
//    protected final List<DBWorkerImpl> workers;
    protected AtomicBoolean onPause = new AtomicBoolean(true);

    private HikariDataSource dataSource;

    public HikariDataSource getDataSource() {
        return dataSource;
    }

//    private static List<Class<?>> entities = Arrays.asList(
//            DiscordServer.class,
//            DiscordServerSettings.class);

    private DBServiceImpl(Properties properties) {
        HikariConfig configuration = new HikariConfig();

        configuration.setJdbcUrl(properties.getProperty("db.url"));
        configuration.setUsername(properties.getProperty("db.username"));
        configuration.setPassword(properties.getProperty("db.password"));
//        configuration.addDataSourceProperty("cachePrepStmts", properties.getProperty("db.cachePrepStmts")); //true
//        configuration.addDataSourceProperty("prepStmtCacheSize", properties.getProperty("db.prepStmtCacheSize")); // 250
//        configuration.addDataSourceProperty("prepStmtCacheSqlLimit", properties.getProperty("db.prepStmtCacheSqlLimit")); // 2048
        configuration.setMinimumIdle(5);

        dataSource = new HikariDataSource(configuration);

        this.tasks = new LinkedBlockingQueue<>();

//        countOfWorkers = Integer.parseInt(properties.getProperty("db.workers.count"));
//        workers = createWorkers();
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

    public static void main(String[] args) throws SQLException {
        Properties properties = new Properties();

        properties.put("db.url", "jdbc:postgresql://localhost/postgres?encoding=utf8&amp;useUnicode=true&amp;");
        properties.put("db.username", "postgres");
        properties.put("db.password", "postgres");

        HikariDataSource dataSource = DBServiceImpl.getInstance(properties).getDataSource();

        ResultSet data = dataSource.getConnection().prepareStatement("SELECT * FROM discordserver").executeQuery();

        int columnCount = data.getMetaData().getColumnCount();
        ResultSetMapProcessor mapper = new ResultSetMapProcessorImpl();
        mapper.addMapper(new DiscordServerSettingsMapper(), DiscordServer.class);

        DiscordServer ds = mapper.asList(data, DiscordServer.class).get(0);

        System.out.println(ds);
    }

//    @Override
//    public void addTask(RunnableFuture<DbData> task) {
//        tasks.add(task);
//    }
//
//    @Override
//    public void start() {
//        onPause.set(false);
//        workers.forEach(Thread::start);
//    }
//
//    @Override
//    public void stop() {
//        onPause.set(true);
//    }
//
//
//    private List<DBWorkerImpl> createWorkers() {
//        List<DBWorkerImpl> workers = new ArrayList<>();
//        for (int i = 0; i < countOfWorkers; i++) {
//            workers.add(new DBWorkerImpl(sessionFactory, tasks, onPause));
//        }
//        return workers;
//    }

}
