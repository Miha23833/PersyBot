package com.persybot.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SqlContainer<E extends DbData> {
    PreparedStatement getById(long id) throws SQLException;
    PreparedStatement update(E updatedEntity) throws SQLException;
    PreparedStatement getByFields(E entity) throws SQLException;
    PreparedStatement insert(E entity) throws SQLException;
}
