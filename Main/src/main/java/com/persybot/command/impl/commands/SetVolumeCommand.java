package com.persybot.command.impl.commands;

import com.persybot.audio.impl.PlayerManagerServiceImpl;
import com.persybot.command.AbstractCommand;
import com.persybot.command.CommandContext;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;

public class SetVolumeCommand extends AbstractCommand {

    private final ServiceAggregatorImpl serviceAggregator;

    public SetVolumeCommand() {
        super(1);
        this.serviceAggregator = ServiceAggregatorImpl.getInstance();
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = new TextCommandValidationResult();
        if (!hasMinimumArgs(args)) {
            validationResult.setInvalid(TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS, "enter volume value.");
        }
        try {
            Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            validationResult.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, "volume must number-like.");
            return validationResult;
        }
        int volume = Integer.parseInt(args.get(0));
        if (volume < 0 || volume > 100) {
            validationResult.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, "volume must be between 0 and 100");
        }

        return validationResult;
    }

    @Override
    public void execute(CommandContext context) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = validateArgs(context.getArgs());

        if (!validationResult.isValid()) {
            context.getEvent().getChannel().sendMessage(validationResult.rejectText()).queue();
            return;
        }

        PlayerManagerServiceImpl.getInstance().setVolume(context.getEvent().getGuild(), Integer.parseInt(context.getArgs().get(0)));
    }

    @Override
    public String describe(CommandContext context) {
        return null;
    }
}
