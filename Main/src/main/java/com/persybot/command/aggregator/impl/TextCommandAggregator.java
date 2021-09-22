package com.persybot.command.aggregator.impl;

import com.persybot.builder.IBuilder;
import com.persybot.command.Command;
import com.persybot.command.aggregator.CommandAggregator;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.utils.EnumUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TextCommandAggregator implements CommandAggregator {
    private final Map<TEXT_COMMAND, Command> commandMap = Collections.synchronizedMap(new HashMap<>());

    private TextCommandAggregator() {

    }

    @Override
    public Command getCommand(String identifier) {
        return commandMap.get(EnumUtils.getEnumIgnoreCase(TEXT_COMMAND.class, identifier));
    }

    public static class Builder implements IBuilder<TextCommandAggregator> {
        private final TextCommandAggregator building;

        public Builder(){
            building = new TextCommandAggregator();
        }

        public Builder addCommand(TEXT_COMMAND textCommand, Command action) {
            building.commandMap.put(textCommand, action);
            return this;
        }

        public Builder removeCommand(TEXT_COMMAND textCommand) {
            building.commandMap.remove(textCommand);
            return this;
        }

        @Override
        public TextCommandAggregator build() {
            return building;
        }
    }
}
