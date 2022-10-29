package com.persybot.config.pojo;

import java.util.Properties;

public class DBConfig {
    public final String hibernateConfigPath;
    public final String connString;
    public final String username;
    public final String password;

    public DBConfig(Properties properties) {
        this.hibernateConfigPath = properties.getProperty("HIBERNATE_CONFIG_PATH");
        this.connString = properties.getProperty("DB_URL");
        this.username = properties.getProperty("DB_USERNAME");
        this.password = properties.getProperty("DB_PASSWORD");
    }
}
