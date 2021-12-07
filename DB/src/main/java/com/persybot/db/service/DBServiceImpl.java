package com.persybot.db.service;

import com.persybot.db.DbData;
import com.persybot.db.SqlContainer;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.entity.mappers.DiscordServerMapper;
import com.persybot.db.entity.mappers.DiscordServerSettingsMapper;
import com.persybot.db.mapper.ResultSetMapProcessor;
import com.persybot.db.mapper.impl.ResultSetMapProcessorImpl;
import com.persybot.db.sql.container.DiscordServerSettingsSqlContainer;
import com.persybot.db.sql.container.DiscordServerSqlContainer;
import com.persybot.db.sql.master.SqlMaster;
import com.persybot.db.sql.sourcereader.SqlSource;
import com.persybot.db.sql.sourcereader.impl.XmlSqlSource;
import com.persybot.logger.impl.PersyBotLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DBServiceImpl implements DBService {
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static volatile DBServiceImpl INSTANCE;

    private final ResultSetMapProcessor mapProcessor;

    private final SqlMaster sqlMaster;
    private final HikariDataSource dataSource;
    private final SqlSource source;

    private DBServiceImpl(Properties properties, SqlSource source) throws SQLException {
        HikariConfig configuration = new HikariConfig();
        this.source = source;

        configuration.setJdbcUrl(properties.getProperty("db.url"));
        configuration.setUsername(properties.getProperty("db.username"));
        configuration.setPassword(properties.getProperty("db.password"));
//        configuration.addDataSourceProperty("cachePrepStmts", properties.getProperty("db.cachePrepStmts")); //true
//        configuration.addDataSourceProperty("prepStmtCacheSize", properties.getProperty("db.prepStmtCacheSize")); // 250
//        configuration.addDataSourceProperty("prepStmtCacheSqlLimit", properties.getProperty("db.prepStmtCacheSqlLimit")); // 2048
        configuration.setMinimumIdle(5);

        this.dataSource = new HikariDataSource(configuration);

        this.sqlMaster = defaultSQLMaster();
        this.mapProcessor = defaultResultSetMapProcessor();
    }

    public static DBServiceImpl getInstance(Properties properties) throws SQLException, IOException, SAXException, ParserConfigurationException {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    String SQLXmlPath = properties.getProperty("db.query.source.SqlXmlPath");
                    String sqlFileDir = properties.getProperty("db.query.source.sqlFileDir");

                    SqlSource source = new XmlSqlSource(SQLXmlPath, sqlFileDir);
                    INSTANCE = new DBServiceImpl(properties, source);
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    private ResultSetMapProcessor defaultResultSetMapProcessor() {
        return new ResultSetMapProcessorImpl()
                .addMapper(new DiscordServerMapper(), DiscordServer.class)
                .addMapper(new DiscordServerSettingsMapper(), DiscordServerSettings.class);
    }

    @Override
    public DiscordServerSettings getDiscordServerSettings(long id) {
        try {
            return mapProcessor.getSingle(
                    container(DiscordServerSettingsSqlContainer.class).getById(id).executeQuery(),
                    DiscordServerSettings.class);
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
            return null;
        }
    }

    @Override
    public void updateDiscordServerSettings(DiscordServerSettings entity) {
        try {
            container(DiscordServerSettingsSqlContainer.class).update(entity).execute();
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
        }
    }

    @Override
    public DiscordServer getDiscordServer(long id) {
        try {
            return mapProcessor.getSingle(
                    container(DiscordServerSqlContainer.class).getById(id).executeQuery(),
                    DiscordServer.class);
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
            return null;
        }
    }

    @Override
    public void updateDiscordServer(DiscordServer entity) {
        try {
            container(DiscordServerSqlContainer.class).update(entity).execute();
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
        }
    }

    private <T extends DbData> List<T> getList(String query, Class<T> tClass) {
        try {
            return mapProcessor.asList(executeQuery(query), tClass);
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
            return null;
        }
    }

    private <T extends DbData> Map<Serializable, T> getMap(String query, Class<T> tClass) {
        try {
            return mapProcessor.map(executeQuery(query), tClass);
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
            return null;
        }
    }

    private ResultSet executeQuery(String query) throws SQLException {
        return this.dataSource.getConnection().prepareStatement(query).executeQuery();
    }

    private SqlMaster defaultSQLMaster() throws SQLException {
        return SqlMaster.builder()
                .connection(this.dataSource.getConnection())
                .source(source)
                .addContainer(DiscordServerSqlContainer.class, DiscordServerSqlContainer::new)
                .addContainer(DiscordServerSettingsSqlContainer.class, DiscordServerSettingsSqlContainer::new)
                .build();
    }

    private  <T extends SqlContainer<?>> T container(Class<T> klass) {
        return this.sqlMaster.container(klass);
    }
}
