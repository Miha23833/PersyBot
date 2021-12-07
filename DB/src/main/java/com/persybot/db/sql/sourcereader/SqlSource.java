package com.persybot.db.sql.sourcereader;

public interface SqlSource {
    String getQuery(String entity, String queryName);
}
