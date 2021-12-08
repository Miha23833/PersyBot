package com.persybot.db.sql.container;

import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.sql.sourcereader.SqlSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DiscordServerSettingsSqlContainer extends AbstractSqlContainer<DiscordServerSettings> {
    private final String entityName = DiscordServerSettings.class.getSimpleName();

    public DiscordServerSettingsSqlContainer(Connection connection, SqlSource sqlSource) {
        super(connection, sqlSource);
    }

    @Override
    protected String entityName() {
        return entityName;
    }

    @Override
    public PreparedStatement getById(long id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.GET_BY_ID.queryId()));
        nullSafeSetLong(statement, 1, id);

        return statement;
    }

    @Override
    public PreparedStatement update(DiscordServerSettings updatedEntity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.UPDATE.queryId()));

        nullSafeSetInt(statement, 1, updatedEntity.getVolume());
        nullSafeSetString(statement, 2, updatedEntity.getPrefix());
        nullSafeSetLong(statement, 3, updatedEntity.getServerId());

        return statement;
    }

    @Override
    public PreparedStatement getByFields(DiscordServerSettings entity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.GET_BY_FIELDS.queryId()));

        nullSafeSetLong(statement, 1, entity.getServerId());
        nullSafeSetLong(statement, 2, entity.getServerId());

        nullSafeSetString(statement, 3, entity.getPrefix());
        nullSafeSetString(statement, 4, entity.getPrefix());

        nullSafeSetInt(statement, 5, entity.getVolume());
        nullSafeSetInt(statement, 6, entity.getVolume());

        return statement;
    }

    @Override
    public PreparedStatement insert(DiscordServerSettings entity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.INSERT.queryId()));

        nullSafeSetLong(statement, 1, entity.getServerId());
        nullSafeSetString(statement, 2, entity.getPrefix());
        nullSafeSetInt(statement, 3 ,entity.getVolume());
        return statement;
    }
}
