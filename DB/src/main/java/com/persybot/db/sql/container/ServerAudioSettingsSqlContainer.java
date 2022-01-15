package com.persybot.db.sql.container;

import com.persybot.db.entity.ServerAudioSettings;
import com.persybot.db.sql.sourcereader.SqlSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServerAudioSettingsSqlContainer extends AbstractSqlContainer<ServerAudioSettings> {
    public ServerAudioSettingsSqlContainer(Connection connection, SqlSource sqlSource) {
        super(connection, sqlSource);
    }

    @Override
    protected String entityName() {
        return ServerAudioSettings.class.getSimpleName();
    }

    @Override
    public PreparedStatement getById(long id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.GET_BY_ID.queryId()));
        nullSafeSetLong(statement, 1, id);
        return statement;
    }

    @Override
    public PreparedStatement update(ServerAudioSettings updatedEntity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.UPDATE.queryId()));
        nullSafeSetString(statement, 1, updatedEntity.getMeetAudioLink());
        nullSafeSetLong(statement, 2, updatedEntity.getIdentifier());
        return statement;
    }

    @Override
    public PreparedStatement getByFields(ServerAudioSettings entity) throws SQLException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public PreparedStatement insert(ServerAudioSettings entity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.INSERT.queryId()));
        nullSafeSetLong(statement, 1, entity.getIdentifier());
        nullSafeSetString(statement, 2, entity.getMeetAudioLink());
        return statement;
    }
}
