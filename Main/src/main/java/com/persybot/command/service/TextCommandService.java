package com.persybot.command.service;

import com.persybot.command.TextCommand;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.service.Service;

public interface TextCommandService extends Service {
    TextCommandService addCommand(TEXT_COMMAND textCommand, TextCommand action);

    TextCommand getCommand(String identifier);
}
