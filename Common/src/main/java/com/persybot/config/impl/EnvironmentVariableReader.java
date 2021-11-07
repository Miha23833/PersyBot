package com.persybot.config.impl;

import com.persybot.config.ConfigSource;

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

        for (String propName : propNames) {
            if (System.getenv().containsKey(propName)) {
                properties.put(propName, System.getenv(propName));
            }
        }

        return properties;
    }
}
