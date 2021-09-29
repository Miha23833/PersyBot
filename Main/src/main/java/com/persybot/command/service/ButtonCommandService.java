package com.persybot.command.service;

import com.persybot.command.ButtonCommand;
import com.persybot.command.service.impl.ButtonCommandServiceImpl;
import com.persybot.enums.BUTTON_ID;
import com.persybot.service.Service;

public interface ButtonCommandService extends Service {
    ButtonCommand getCommand(String identifier);

    ButtonCommandServiceImpl addCommand(BUTTON_ID button_id, ButtonCommand action);
}
