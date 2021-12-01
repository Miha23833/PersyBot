package com.persybot.db.service;

import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.entity.mappers.DiscordServerMapper;
import com.persybot.db.entity.mappers.DiscordServerSettingsMapper;
import com.persybot.db.mapper.ResultSetMapProcessor;
import com.persybot.db.mapper.impl.ResultSetMapProcessorImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DBServiceImpl {
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static volatile DBServiceImpl INSTANCE;

    private final ResultSetMapProcessor mapProcessor;

    private final HikariDataSource dataSource;

    private DBServiceImpl(Properties properties) {
        this.mapProcessor = new ResultSetMapProcessorImpl();
        populateDefaultMappers(mapProcessor);

        HikariConfig configuration = new HikariConfig();

        configuration.setJdbcUrl(properties.getProperty("db.url"));
        configuration.setUsername(properties.getProperty("db.username"));
        configuration.setPassword(properties.getProperty("db.password"));
//        configuration.addDataSourceProperty("cachePrepStmts", properties.getProperty("db.cachePrepStmts")); //true
//        configuration.addDataSourceProperty("prepStmtCacheSize", properties.getProperty("db.prepStmtCacheSize")); // 250
//        configuration.addDataSourceProperty("prepStmtCacheSqlLimit", properties.getProperty("db.prepStmtCacheSqlLimit")); // 2048
        configuration.setMinimumIdle(5);

        this.dataSource = new HikariDataSource(configuration);
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

    private void populateDefaultMappers(ResultSetMapProcessor processor) {
        processor.addMapper(new DiscordServerMapper(), DiscordServer.class);
        processor.addMapper(new DiscordServerSettingsMapper(), DiscordServerSettings.class);
    }

}
