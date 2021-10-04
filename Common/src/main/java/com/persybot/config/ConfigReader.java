package com.persybot.config;

import java.io.IOException;
import java.util.Properties;

public interface ConfigReader {
    void loadFile(String path) throws IOException;

    Properties getProperties();
}
