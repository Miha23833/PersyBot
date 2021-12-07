package com.persybot.db.sql.container;

import com.persybot.db.DbData;
import com.persybot.db.SqlContainer;
import com.persybot.db.sql.sourcereader.SqlSource;

import java.sql.Connection;

public abstract class AbstractSqlContainer<T extends DbData> implements SqlContainer<T> {
    protected enum DefaultQueryId {
        GET_BY_ID("getById"),
        UPDATE("update"),
        GET_BY_FIELDS("getByFields");

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
}
