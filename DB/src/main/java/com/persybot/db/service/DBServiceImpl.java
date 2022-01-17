package com.persybot.db.service;

import com.persybot.db.SqlContainer;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.entity.EqualizerPreset;
import com.persybot.db.entity.PlayList;
import com.persybot.db.entity.ServerAudioSettings;
import com.persybot.db.entity.mappers.DiscordServerMapper;
import com.persybot.db.entity.mappers.DiscordServerSettingsMapper;
import com.persybot.db.entity.mappers.EqualizerPresetMapper;
import com.persybot.db.entity.mappers.PlayListMapper;
import com.persybot.db.entity.mappers.ServerAudioSettingsMapper;
import com.persybot.db.mapper.ResultSetMapProcessor;
import com.persybot.db.mapper.impl.ResultSetMapProcessorImpl;
import com.persybot.db.sql.container.DiscordServerSettingsSqlContainer;
import com.persybot.db.sql.container.DiscordServerSqlContainer;
import com.persybot.db.sql.container.EqualizerPresetSqlContainer;
import com.persybot.db.sql.container.PlayListSqlContainer;
import com.persybot.db.sql.container.ServerAudioSettingsSqlContainer;
import com.persybot.db.sql.master.SqlMaster;
import com.persybot.db.sql.sourcereader.SqlSource;
import com.persybot.db.sql.sourcereader.impl.XmlSqlSource;
import com.persybot.logger.impl.PersyBotLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Override
    public Optional<Long> saveDiscordServerSettings(DiscordServerSettings entity) {
        try {
            return Optional.ofNullable(mapProcessor.getSingleLong(container(DiscordServerSettingsSqlContainer.class).insert(entity).executeQuery()));
        } catch (SQLException | IllegalArgumentException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<DiscordServerSettings> getDiscordServerSettings(long id) {
        try {
            return Optional.ofNullable(mapProcessor.getSingle(
                    container(DiscordServerSettingsSqlContainer.class).getById(id).executeQuery(),
                    DiscordServerSettings.class));
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean updateDiscordServerSettings(DiscordServerSettings entity) {
        try {
            container(DiscordServerSettingsSqlContainer.class).update(entity).execute();
            return true;
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<Long> saveDiscordServer(DiscordServer entity) {
        try {
            return Optional.ofNullable(mapProcessor.getSingleLong(container(DiscordServerSqlContainer.class).insert(entity).executeQuery()));
        } catch (SQLException | IllegalArgumentException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<DiscordServer> getDiscordServer(long id) {
        try {
            return Optional.ofNullable(mapProcessor.getSingle(
                    container(DiscordServerSqlContainer.class).getById(id).executeQuery(),
                    DiscordServer.class));
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean updateDiscordServer(DiscordServer entity) {
        try {
            container(DiscordServerSqlContainer.class).update(entity).execute();
            return true;
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<PlayList> getPlaylistById(long id) {
        try {
            return Optional.ofNullable(mapProcessor.getSingle(
                    container(PlayListSqlContainer.class).getById(id).executeQuery(),
                    PlayList.class));
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Optional<PlayList> getPlaylistByName(String name, long serverId) {
        PlayList entity = new PlayList(null, serverId, name, null);
        try {
            return Optional.ofNullable(mapProcessor.getSingle(
                    container(PlayListSqlContainer.class).getByFields(entity).executeQuery(),
                    PlayList.class));
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Optional<Map<Long, PlayList>> getAllPlaylistForServer(Long serverId) {
        try {
            return Optional.ofNullable(mapProcessor.map(
                    container(PlayListSqlContainer.class).getAllPlaylistForServer(serverId).executeQuery(),
                    PlayList.class));
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Optional<Long> saveOrUpdatePlayList(PlayList playList) {
        if (isPlaylistExists(playList)) {
            updatePlayList(playList);
            return Optional.ofNullable(playList.getId());
        } else {
            return savePlayList(playList);
        }
    }

    @Override
    public boolean isPlaylistExists(PlayList entity) {
        try {
            return mapProcessor.getSingleBoolean(container(PlayListSqlContainer.class).isPlayListExists(entity).executeQuery());
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<Long> savePlayList(PlayList entity) {
        try {
            return Optional.ofNullable(mapProcessor.getSingleLong(container(PlayListSqlContainer.class).insert(entity).executeQuery()));
        } catch (SQLException | IllegalArgumentException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> saveServerAudioSettings(ServerAudioSettings entity) {
        try {
            return Optional.ofNullable(mapProcessor.getSingleLong(container(ServerAudioSettingsSqlContainer.class).insert(entity).executeQuery()));
        } catch (SQLException | IllegalArgumentException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ServerAudioSettings> getServerAudioSettings(long id) {
        try {
            return Optional.ofNullable(mapProcessor.getSingle(
                    container(ServerAudioSettingsSqlContainer.class).getById(id).executeQuery(),
                    ServerAudioSettings.class));
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean updateServerAudioSettings(ServerAudioSettings entity) {
        try {
            container(ServerAudioSettingsSqlContainer.class).update(entity).execute();
            return true;
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<EqualizerPreset> getEqPresetByName(String name) {
        try {
            return Optional.ofNullable(mapProcessor.getSingle(
                    container(EqualizerPresetSqlContainer.class).getByName(name).executeQuery(),
                    EqualizerPreset.class));
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<EqualizerPreset>> getAllEqPresets() {
        try {
            return Optional.ofNullable(mapProcessor.asList(
                    container(EqualizerPresetSqlContainer.class).getAll().executeQuery(),
                    EqualizerPreset.class));
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean updatePlayList(PlayList entity) {
        try {
            container(PlayListSqlContainer.class).update(entity).execute();
            return true;
        } catch (SQLException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    private SqlMaster defaultSQLMaster() throws SQLException {
        return SqlMaster.builder()
                .connection(this.dataSource.getConnection())
                .source(source)
                .addContainer(DiscordServerSqlContainer.class, DiscordServerSqlContainer::new)
                .addContainer(DiscordServerSettingsSqlContainer.class, DiscordServerSettingsSqlContainer::new)
                .addContainer(PlayListSqlContainer.class, PlayListSqlContainer::new)
                .addContainer(ServerAudioSettingsSqlContainer.class, ServerAudioSettingsSqlContainer::new)
                .addContainer(EqualizerPresetSqlContainer.class, EqualizerPresetSqlContainer::new)
                .build();
    }

    private  <T extends SqlContainer<?>> T container(Class<T> klass) {
        return this.sqlMaster.container(klass);
    }

    private ResultSetMapProcessor defaultResultSetMapProcessor() {
        return new ResultSetMapProcessorImpl()
                .addMapper(DiscordServer.class, new DiscordServerMapper())
                .addMapper(DiscordServerSettings.class, new DiscordServerSettingsMapper())
                .addMapper(PlayList.class, new PlayListMapper())
                .addMapper(ServerAudioSettings.class, new ServerAudioSettingsMapper())
                .addMapper(EqualizerPreset.class, new EqualizerPresetMapper());
    }
}
