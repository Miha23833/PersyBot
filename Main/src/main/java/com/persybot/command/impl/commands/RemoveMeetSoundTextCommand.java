package com.persybot.command.impl.commands;

import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.service.DBService;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;
import java.util.Optional;

public class RemoveMeetSoundTextCommand extends AbstractTextCommand {
    private final DBService dbService;
    public RemoveMeetSoundTextCommand() {
        super(0);
        this.dbService = ServiceAggregator.getInstance().get(DBService.class);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        DiscordServer discordServer = dbService
                .read(context.getGuildId(), DiscordServer.class)
                .orElseThrow(() -> new RuntimeException("Could not read discord server with id = " + context.getGuildId()));

        discordServer.getSettings().setMeetAudioLink(null);

        Optional<DiscordServer> queryResult = dbService.update(discordServer);

        if (queryResult.isEmpty()) {
            context.getEvent().getChannel().sendMessage(new DefaultTextMessage("Failed to remove meet track").template()).queue();
            return false;
        }
        return true;
    }

    @Override
    protected boolean runAfter(TextCommandContext context) {
        context.getEvent().getChannel().sendMessage(new DefaultTextMessage("Meet track removed").template()).queue();
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return "Removes meet track and will you longer hear it before your tracks queue";
    }
}
