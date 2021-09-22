package com.persybot.config.impl;

import com.persybot.config.ConfigReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReaderImpl implements ConfigReader {
    Properties properties;

    public ConfigReaderImpl(String path) throws IOException {
        properties = new Properties();
        loadFile(path);
    }

    @Override
    public void loadFile(String path) throws IOException {
        try(FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        }
    }

    @Override
    public String getProperty(String path) {
        return properties.getProperty(path);
    }
}
