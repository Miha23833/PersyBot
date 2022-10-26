package com.persybot.config.impl;

import com.persybot.config.ConfigSource;
import com.persybot.logger.impl.PersyBotLogger;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class EnvironmentVariableReader implements ConfigSource {
    private final Set<String> propNames = new HashSet<>();

    public EnvironmentVariableReader requireProperty(String propName) {
        this.propNames.add(propName);
        return this;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();

        // TODO: remove log or add it to debug
        for (String propName : propNames) {
            if (System.getenv().containsKey(propName)) {
                PersyBotLogger.BOT_LOGGER.info("Read environment variable: " + propName + ": " + System.getenv(propName));
                properties.put(propName, System.getenv(propName));
            } else {
                PersyBotLogger.BOT_LOGGER.info("Could not find environment variable: " + propName);
            }
        }

        return properties;
    }
}
