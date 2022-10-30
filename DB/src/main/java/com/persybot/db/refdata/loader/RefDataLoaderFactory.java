package com.persybot.db.refdata.loader;

import com.persybot.logger.impl.PersyBotLogger;

import java.util.ArrayList;
import java.util.List;

public class RefDataLoaderFactory {
    private final List<RefDataLoader<?>> loaders = new ArrayList<>();

    public RefDataLoaderFactory() {}

    public RefDataLoaderFactory addLoader(RefDataLoader<?> loader) {
        this.loaders.add(loader);
        return this;
    }

    public void process() {
        for (RefDataLoader<?> loader: loaders) {
            try {
                loader.loadRefData();
            } catch (Exception e) {
                PersyBotLogger.BOT_LOGGER.error(e);
            }
        }
    }
}
