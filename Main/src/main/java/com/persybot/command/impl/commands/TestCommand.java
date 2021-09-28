package com.persybot.command.impl.commands;

import com.persybot.command.AbstractCommand;
import com.persybot.command.CommandContext;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.impl.PlayerMessage;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;

public class TestCommand extends AbstractCommand {
    public TestCommand() {
        super(0);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }

    @Override
    public void execute(CommandContext context) {
        context.getEvent().getChannel().sendMessage(new PlayerMessage("ABOBA", false, false).getMessage()).queue();
    }

    @Override
    public String describe(CommandContext context) {
        return "";
    }
}
