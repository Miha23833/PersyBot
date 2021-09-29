package com.persybot.command;

public interface ButtonCommand {
    void execute(ButtonCommandContext context);

    String describe(ButtonCommandContext context);
}
