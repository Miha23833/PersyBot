package com.persybot.command.impl.commands;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.db.model.impl.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;

public class ChangePrefixCommand extends AbstractTextCommand {
    private final int maxPrefixLen;

    public ChangePrefixCommand(int maxPrefixLen) {
        super(1);
        this.maxPrefixLen = maxPrefixLen;
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> rsp = new TextCommandValidationResult();
        if (args == null || args.size() < 1 || args.get(0) == null || args.get(0).isBlank()) {
            rsp.setInvalid(TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS, "Please, set new prefix.");
            return rsp;
        }
        if (args.get(0).length() > 3) {
            rsp.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, String.join(" ", "Max length of prefix is ", String.valueOf(maxPrefixLen), "."));
        }

        return rsp;
    }

    @Override
    public void execute(TextCommandContext context) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = validateArgs(context.getArgs());

        if (!validationResult.isValid()) {
            context.getEvent().getChannel().sendMessage(new DefaultTextMessage(validationResult.rejectText()).template()).queue();
            return;
        }
        String prefix = context.getArgs().get(0);

        Channel channel = ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(context.getEvent().getGuild().getIdLong());

        DiscordServerSettings serverSettings = channel.getServerSettings();
        serverSettings.setPrefix(prefix);

        ServiceAggregatorImpl.getInstance().getService(DBService.class).update(serverSettings);

        context.getEvent().getChannel().sendMessage(new DefaultTextMessage(String.join("","Prefix updated to ", "'", prefix, "'")).template()).queue();
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}