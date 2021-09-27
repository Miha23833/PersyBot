package com.persybot.command.service.impl;

import com.persybot.command.Command;
import com.persybot.command.service.TextCommandService;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.utils.EnumUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TextCommandServiceImpl implements TextCommandService {
    private static TextCommandServiceImpl INSTANCE;
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public static TextCommandServiceImpl getInstance() {
        if (INSTANCE == null) {
            try {
                rwLock.writeLock().lock();
                if (INSTANCE == null) {
                    INSTANCE = new TextCommandServiceImpl();
                }
            } finally {
                rwLock.writeLock().unlock();
            }
        }
        return INSTANCE;
    }

    private TextCommandServiceImpl(){}

    private final Map<TEXT_COMMAND, Command> commandMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Command getCommand(String identifier) {
        return commandMap.get(EnumUtils.getEnumIgnoreCase(TEXT_COMMAND.class, identifier));
    }

    public TextCommandServiceImpl addCommand(TEXT_COMMAND textCommand, Command action) {
        commandMap.put(textCommand, action);
        return this;
    }
}