package com.persybot.command.impl.commands;

import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.service.DBService;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;

import static com.persybot.utils.URLUtil.isPlayableLink;
import static com.persybot.utils.URLUtil.isUrl;

public class AddMeetSoundTextCommand extends AbstractTextCommand {
    private final DBService dbService;

    public AddMeetSoundTextCommand() {
        super(1);
        this.dbService = ServiceAggregator.getInstance().get(DBService.class);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> result = new TextCommandValidationResult();

        if (!hasMinimumArgs(args)) {
            result.setInvalid(TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS, "Provide link to track");
            return result;
        }

        // TODO: add check if link is playable
        if (!isUrl(args.get(0))) {
            result.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, "Argument must be url");
            return result;
        }
        if (!isPlayableLink(args.get(0))) {
            result.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, "I cannot play this url");
            return result;
        }

        return result;
    }

    @Override
    protected boolean runBefore(TextCommandContext context) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = validateArgs(context.getArgs());
        if (validationResult.isValid()) {
            return true;
        }
        context.getEvent().getChannel().sendMessage(validationResult.rejectText()).queue();
        return false;
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        DiscordServer discordServer = dbService.readAssured(context.getGuildId(), DiscordServer.class);

        discordServer.getSettings().setMeetAudioLink(context.getArgs().get(0));
        dbService.update(discordServer);
        return true;
    }

    @Override
    protected boolean runAfter(TextCommandContext context) {
        context.getEvent().getChannel().sendMessage(new InfoMessage("Success", "Meeting track was updated").template()).queue();
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return "Use it to set track that bot will play every time when it starts play new queue";
    }
}
