package com.persybot.config.impl;

import com.persybot.config.ConfigSource;
import com.persybot.config.MasterConfig;
import com.persybot.logger.impl.PersyBotLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MasterConfigImpl implements MasterConfig, ConfigSource {
    private final List<ConfigSource> configSources = new ArrayList<>(); 
    
    @Override
    public MasterConfig addConfigSource(ConfigSource source) {
        configSources.add(source);
        return this;
    }

    @Override
    public Properties getProperties() {
        Properties result = new Properties();
        
        for (ConfigSource source : configSources) {
            Properties properties = source.getProperties();
            loadProperties(result, properties);
        }
        
        return result;
    }
    
    private void loadProperties(Properties dest, Properties src) {
        for (Object propKey : src.keySet()) {
            if (dest.contains(propKey)) {
                if (dest.get(propKey).equals(src.get(propKey))) {
                    String text = String.format("The result properties already has property with key %s and same value.", propKey);

                    PersyBotLogger.BOT_LOGGER.warn(text);
                } else {
                    String text = String.format("The result properties already has property with key %s, but new value is different: %s. It will be ignored.", propKey, src.get(propKey));

                    PersyBotLogger.BOT_LOGGER.warn(text);
                }
            }
            else {
                dest.put(propKey, src.get(propKey));
            }
        }
    }
}
