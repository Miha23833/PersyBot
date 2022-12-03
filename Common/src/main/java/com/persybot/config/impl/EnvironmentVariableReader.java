package com.persybot.config.impl;

import com.persybot.config.ConfigSource;
import com.persybot.logger.impl.PersyBotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EnvironmentVariableReader implements ConfigSource {
    private final List<EnvironmentVariableNamespace> props = new ArrayList<>();

    public EnvironmentVariableReader requireProperty(String propName, String... aliases) {
        this.props.add(new EnvironmentVariableNamespace(propName, aliases));
        return this;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();

        for (EnvironmentVariableNamespace propNamespace: props) {
            boolean found = false;
            for (String alias : propNamespace.getAllPossibleNames()) {
                if (System.getenv().containsKey(alias)) {
                    properties.put(propNamespace.getPropName(), System.getenv(alias));
                    found = true;
                    break;
                }
            }
            if (!found) {
                PersyBotLogger.BOT_LOGGER.info("Could not find environment variable: " + propNamespace.getPropName() +
                        " (With aliases: [" + String.join(", ", propNamespace.aliases) + "])");
            }
        }

        return properties;
    }

    private static class EnvironmentVariableNamespace {
        final String propName;
        final String[] aliases;

        public EnvironmentVariableNamespace(String propName, String[] aliases) {
            this.propName = propName;
            this.aliases = aliases;
        }

        public String getPropName() {
            return this.propName;
        }

        public String[] getAllPossibleNames() {
            String[] result = new String[aliases.length + 1];
            System.arraycopy(aliases, 0, result, 0, aliases.length);
            result[result.length - 1] = propName;
            return result;
        }
    }
}
