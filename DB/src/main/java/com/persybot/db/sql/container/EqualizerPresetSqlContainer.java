package com.persybot.db.sql.container;

import com.persybot.db.entity.EqualizerPreset;
import com.persybot.db.sql.sourcereader.SqlSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EqualizerPresetSqlContainer extends AbstractSqlContainer<EqualizerPreset> {
    public EqualizerPresetSqlContainer(Connection connection, SqlSource sqlSource) {
        super(connection, sqlSource);
    }

    @Override
    protected String entityName() {
        return EqualizerPreset.class.getSimpleName();
    }

    @Override
    public PreparedStatement getById(long id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query(DefaultQueryId.GET_BY_ID.queryId()));
        nullSafeSetLong(statement, 1, id);

        return statement;
    }

    @Override
    public PreparedStatement update(EqualizerPreset updatedEntity) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public PreparedStatement getByFields(EqualizerPreset entity) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public PreparedStatement insert(EqualizerPreset entity) {
        throw new RuntimeException("Not implemented");
    }

    public PreparedStatement getByName(String name) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(query("getByName"));
        nullSafeSetString(statement, 1, name);

        return statement;
    }

    public PreparedStatement getAll() throws SQLException {
        return this.connection.prepareStatement(query("getAll"));
    }
}
