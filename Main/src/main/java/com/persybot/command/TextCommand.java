package com.persybot.command;

public interface TextCommand {
    void execute(TextCommandContext context);

    String describe(TextCommandContext context);
}
