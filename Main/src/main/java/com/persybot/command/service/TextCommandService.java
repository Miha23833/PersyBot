package com.persybot.command.service;

import com.persybot.command.TextCommand;
import com.persybot.enums.TEXT_COMMAND;

public interface TextCommandService {
    TextCommandService addCommand(TEXT_COMMAND textCommand, TextCommand action);

    TextCommand getCommand(String identifier);

    boolean containsCommand(String command);
}
