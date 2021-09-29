package com.persybot.command.service.impl;

import com.persybot.command.ButtonCommand;
import com.persybot.command.service.ButtonCommandService;
import com.persybot.enums.BUTTON_ID;
import com.persybot.utils.EnumUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ButtonCommandServiceImpl implements ButtonCommandService {
    private static ButtonCommandServiceImpl INSTANCE;
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public static ButtonCommandServiceImpl getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new ButtonCommandServiceImpl();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    private ButtonCommandServiceImpl(){}

    private final Map<BUTTON_ID, ButtonCommand> commandMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public ButtonCommand getCommand(String identifier) {
        return commandMap.get(EnumUtils.getEnumIgnoreCase(BUTTON_ID.class, identifier));
    }

    @Override
    public ButtonCommandServiceImpl addCommand(BUTTON_ID button_id, ButtonCommand action) {
        commandMap.put(button_id, action);
        return this;
    }
}
