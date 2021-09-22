package com.persybot.command.aggregator;

import com.persybot.command.Command;

public interface CommandAggregator {
    Command getCommand(String identifier);
}
