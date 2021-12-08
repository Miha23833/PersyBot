package com.persybot.db.sql.container;

import com.persybot.db.entity.PlayList;
import com.persybot.db.sql.sourcereader.SqlSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayListSqlContainer extends AbstractSqlContainer<PlayList> {
    private enum PlayListQueryId {
        GET_BY_SERVER_ID("getByServerId");

        private String queryId;
        PlayListQueryId(String queryId) {
            this.queryId = queryId;
        }

        public String queryId() {
            return this.queryId;
        }
    }

    public PlayListSqlContainer(Connection connection, SqlSource sqlSource) {
        super(connection, sqlSource);
    }

    @Override
    protected String entityName() {
        return PlayList.class.getSimpleName();
    }

    @Override
    public PreparedStatement getById(long id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(DefaultQueryId.GET_BY_ID.queryId());

        nullSafeSetLong(statement, 1, id);

        return statement;
    }

    @Override
    public PreparedStatement update(PlayList updatedEntity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.UPDATE.queryId()));

        nullSafeSetString(statement, 1, updatedEntity.getName());
        nullSafeSetString(statement, 2, updatedEntity.getUrl());
        nullSafeSetLong(statement, 3, updatedEntity.getId());

        return statement;
    }

    @Override
    public PreparedStatement getByFields(PlayList entity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.GET_BY_FIELDS.queryId()));

        nullSafeSetLong(statement, 1, entity.getServerId());
        nullSafeSetLong(statement, 2, entity.getServerId());

        nullSafeSetLong(statement, 3, entity.getId());
        nullSafeSetLong(statement, 4, entity.getId());

        nullSafeSetString(statement, 5, entity.getName());
        nullSafeSetString(statement, 6, entity.getName());

        return statement;
    }

    public PreparedStatement getAllPlaylistForServer(long serverId) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(PlayListQueryId.GET_BY_SERVER_ID.queryId()));
        statement.setLong(1, serverId);
        return statement;
    }

    public PreparedStatement isPlayListExists(PlayList entity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.EXISTS.queryId()));

        nullSafeSetLong(statement, 1, entity.getId());
        nullSafeSetLong(statement, 2, entity.getId());

        nullSafeSetLong(statement, 3, entity.getServerId());
        nullSafeSetLong(statement, 4, entity.getServerId());

        nullSafeSetString(statement, 5, entity.getName());
        nullSafeSetString(statement, 6, entity.getName());

        return statement;
    }

    @Override
    public PreparedStatement insert(PlayList entity) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.INSERT.queryId()));

        nullSafeSetLong(statement, 1, entity.getServerId());
        nullSafeSetString(statement, 2, entity.getName());
        nullSafeSetString(statement, 3, entity.getUrl());
        nullSafeSetString(statement, 4, entity.getUrl());

        return statement;
    }
}
