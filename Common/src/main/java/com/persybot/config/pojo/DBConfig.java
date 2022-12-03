package com.persybot.config.pojo;

import java.util.Properties;

public class DBConfig {
    public final String hibernateConfigPath;
    public final String connString;
    public final String username;
    public final String password;

    public DBConfig(Properties properties) {
        this.hibernateConfigPath = properties.getProperty("hibernate.config_path");
        this.connString = properties.getProperty("hibernate.connection.url");
        this.username = properties.getProperty("hibernate.connection.username");
        this.password = properties.getProperty("hibernate.connection.password");
    }
}
