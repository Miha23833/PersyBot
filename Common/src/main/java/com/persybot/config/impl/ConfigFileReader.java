package com.persybot.config.impl;

import com.persybot.config.ConfigSource;
import com.persybot.logger.impl.PersyBotLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader implements ConfigSource {
    private final String path;

    public ConfigFileReader(String path) {
        this.path = path;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();

        try(FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
        }

        return properties;
    }
}
