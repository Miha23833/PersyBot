package com.persybot.command.service.impl;

import com.persybot.command.ButtonCommand;
import com.persybot.command.service.ButtonCommandService;
import com.persybot.enums.BUTTON_ID;
import com.persybot.utils.EnumUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ButtonCommandContainerImpl implements ButtonCommandService {
    public ButtonCommandContainerImpl(){}

    private final Map<BUTTON_ID, ButtonCommand> commandMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public ButtonCommand getCommand(String identifier) {
        return commandMap.get(EnumUtils.getEnumIgnoreCase(BUTTON_ID.class, identifier));
    }

    @Override
    public ButtonCommandContainerImpl addCommand(BUTTON_ID button_id, ButtonCommand action) {
        commandMap.put(button_id, action);
        return this;
    }
}
