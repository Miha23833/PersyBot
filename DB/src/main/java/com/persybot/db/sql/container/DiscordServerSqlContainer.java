package com.persybot.db.sql.container;

import com.persybot.db.entity.DiscordServer;
import com.persybot.db.sql.sourcereader.SqlSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DiscordServerSqlContainer extends AbstractSqlContainer<DiscordServer>{
    private final String entityName = DiscordServer.class.getSimpleName();

    public DiscordServerSqlContainer(Connection connection, SqlSource sqlSource) {
        super(connection, sqlSource);
    }

    @Override
    protected String entityName() {
        return entityName;
    }

    @Override
    public PreparedStatement getById(long id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("");
        statement.setLong(1, id);
        return statement;
    }

    @Override
    public PreparedStatement update(DiscordServer updatedEntity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.UPDATE.queryId()));
        statement.setLong(1, updatedEntity.getLanguageId());
        statement.setLong(2, updatedEntity.getServerId());
        return statement;
    }

    @Override
    public PreparedStatement getByFields(DiscordServer entity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.GET_BY_FIELDS.queryId()));
        statement.setLong(1, entity.getLanguageId());
        statement.setLong(2, entity.getServerId());
        return statement;
    }
}