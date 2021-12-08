package com.persybot.db.sql.container;

import com.persybot.db.DbData;
import com.persybot.db.SqlContainer;
import com.persybot.db.sql.sourcereader.SqlSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public abstract class AbstractSqlContainer<T extends DbData> implements SqlContainer<T> {
    protected enum DefaultQueryId {
        GET_BY_ID("getById"),
        UPDATE("update"),
        GET_BY_FIELDS("getByFields"),
        EXISTS("exists"),
        INSERT("insert");

        private final String queryId;
        DefaultQueryId(String queryId) {
            this.queryId = queryId;
        }

        public String queryId() {
            return this.queryId;
        }
    }

    protected final Connection connection;
    protected final SqlSource sqlSource;
    public AbstractSqlContainer(Connection connection, SqlSource sqlSource) {
        this.connection = connection;
        this.sqlSource = sqlSource;
    }

    protected abstract String entityName();

    protected final String query(String queryId) {
        return this.sqlSource.getQuery(entityName(), queryId);
    }

    protected final void nullSafeSetLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }

    protected final void nullSafeSetInt(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    protected final void nullSafeSetString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

}
