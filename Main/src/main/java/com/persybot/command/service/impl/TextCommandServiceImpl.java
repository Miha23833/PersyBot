package com.persybot.command.service.impl;

import com.persybot.command.TextCommand;
import com.persybot.command.service.TextCommandService;
import com.persybot.enums.TEXT_COMMAND;

import java.util.Arrays;
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

    private final Map<String, TextCommand> commandMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public TextCommand getCommand(String identifier) {
        return commandMap.get(identifier);
    }

    @Override
    public boolean containsCommand(String command) {
        return this.commandMap.containsKey(command);
    }

    @Override
    public TextCommandServiceImpl addCommand(TEXT_COMMAND textCommand, TextCommand action) {
        Arrays.stream(textCommand.getAliases()).forEach(alias -> commandMap.put(alias, action));
        return this;
    }
}
