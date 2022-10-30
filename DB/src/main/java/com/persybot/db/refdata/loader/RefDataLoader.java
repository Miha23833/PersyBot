package com.persybot.db.refdata.loader;

import com.persybot.db.entity.DBEntity;

public interface RefDataLoader<T extends DBEntity> {
    void loadRefData();
}
