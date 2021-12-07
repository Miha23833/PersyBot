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
        statement.setLong(1, id);
        return statement;
    }

    @Override
    public PreparedStatement update(DiscordServerSettings updatedEntity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.UPDATE.queryId()));
        statement.setLong(1, updatedEntity.getVolume());
        statement.setString(2, updatedEntity.getPrefix());
        statement.setLong(3, updatedEntity.getServerId());
        return statement;
    }

    @Override
    public PreparedStatement getByFields(DiscordServerSettings entity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.GET_BY_FIELDS.queryId()));
        statement.setLong(1, entity.getServerId());
        statement.setString(2, entity.getPrefix());
        statement.setLong(3, entity.getVolume());
        return statement;
    }
}
