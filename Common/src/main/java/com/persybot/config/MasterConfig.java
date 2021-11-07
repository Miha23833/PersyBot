package com.persybot.config;

import java.util.Properties;

public interface MasterConfig {
    MasterConfig addConfigSource(ConfigSource source);

    Properties getProperties();
}
