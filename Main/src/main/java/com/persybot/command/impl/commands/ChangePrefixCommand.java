package com.persybot.command.impl.commands;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.config.pojo.BotConfig;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.utils.BotUtils;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;

public class ChangePrefixCommand extends AbstractTextCommand {
    private final int maxPrefixLen;

    public ChangePrefixCommand(BotConfig botConfig) {
        super(1);
        this.maxPrefixLen = botConfig.maxPrefixLen;
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> rsp = new TextCommandValidationResult();
        if (args == null || args.size() < 1 || args.get(0) == null || args.get(0).isBlank()) {
            rsp.setInvalid(TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS, "Please, set new prefix");
            return rsp;
        }
        if (args.get(0).length() > 3) {
            rsp.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, String.join(" ", "Max length of prefix is ", String.valueOf(maxPrefixLen)));
        }

        return rsp;
    }

    @Override
    protected boolean runBefore(TextCommandContext context) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = validateArgs(context.getArgs());

        if (!validationResult.isValid()) {
            BotUtils.sendMessage(new DefaultTextMessage(validationResult.rejectText()).template(), context.getEvent().getChannel().asTextChannel());
            return false;
        }
        return true;
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        String prefix = context.getArgs().get(0);

        Channel channel = ServiceAggregator.getInstance().get(ChannelService.class).getChannel(context.getEvent().getGuild().getIdLong());

        DiscordServerSettings serverSettings = channel.getDiscordServer().getSettings();
        serverSettings.setPrefix(prefix);

        ServiceAggregator.getInstance().get(DBService.class).update(channel.getDiscordServer());

        BotUtils.sendMessage(new DefaultTextMessage(String.join("","Prefix updated to ", "'", prefix, "'")).template(), context.getEvent().getChannel().asTextChannel());
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}
