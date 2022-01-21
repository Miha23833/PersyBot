package com.persybot.command.service;

import com.persybot.command.ButtonCommand;
import com.persybot.command.service.impl.ButtonCommandContainerImpl;
import com.persybot.enums.BUTTON_ID;

public interface ButtonCommandService {
    ButtonCommand getCommand(String identifier);

    ButtonCommandContainerImpl addCommand(BUTTON_ID button_id, ButtonCommand action);
}
