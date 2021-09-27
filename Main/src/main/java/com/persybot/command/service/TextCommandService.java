package com.persybot.command.service;

import com.persybot.command.Command;
import com.persybot.service.Service;

public interface TextCommandService extends Service {
    Command getCommand(String identifier);
}
