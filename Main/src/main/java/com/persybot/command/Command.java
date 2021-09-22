package com.persybot.command;

public interface Command {
    void execute(CommandContext context);

    String describe(CommandContext context);
}
