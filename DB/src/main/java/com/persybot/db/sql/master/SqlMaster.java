package com.persybot.db.sql.master;

import com.persybot.builder.IBuilder;
import com.persybot.db.SqlContainer;
import com.persybot.db.sql.sourcereader.SqlSource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class SqlMaster {
    private final Map<Class<? extends SqlContainer<?>>, SqlContainer<?>> containers = new HashMap<>();
    private Connection connection;
    private SqlSource sqlSource;

    private SqlMaster() {

    }

    public <T extends SqlContainer<?>> T container(Class<T> klass) {
        return klass.cast(containers.get(klass));
    }

    public static SqlMasterBuilder builder() {
        return new SqlMasterBuilder();
    }

    public static class SqlMasterBuilder implements IBuilder<SqlMaster> {
        private final SqlMaster sqlMaster;

        public SqlMasterBuilder() {
            sqlMaster = new SqlMaster();
        }

        public SqlMasterBuilder connection(Connection connection) {
            this.sqlMaster.connection = connection;
            return this;
        }

        public SqlMasterBuilder source(SqlSource source) {
            this.sqlMaster.sqlSource = source;
            return this;
        }

        public <T extends SqlContainer<?>> SqlMasterBuilder addContainer(Class<T> identifierClass, BiFunction<Connection, SqlSource, T> newInstance) {
            this.sqlMaster.containers.put(identifierClass, newInstance.apply(this.sqlMaster.connection, this.sqlMaster.sqlSource));
            return this;
        }

        @Override
        public SqlMaster build() {
            return this.sqlMaster;
        }
    }
}
