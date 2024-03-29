package com.persybot.command.impl.commands;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.utils.BotUtils;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;

public class SetVolumeTextCommand extends AbstractTextCommand {

    public SetVolumeTextCommand() {
        super(1);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = new TextCommandValidationResult();
        if (!hasMinimumArgs(args)) {
            validationResult.setInvalid(TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS, "Enter volume value");
            return validationResult;
        }
        try {
            Byte.parseByte(args.get(0));
        } catch (NumberFormatException e) {
            validationResult.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, "Volume must number-like");
            return validationResult;
        }
        byte volume = Byte.parseByte(args.get(0));
        if (volume < 0 || volume > 100) {
            validationResult.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, "Volume must be between 0 and 100");
            return validationResult;
        }

        return validationResult;
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
        long channelId = context.getEvent().getGuild().getIdLong();
        Channel channel = ServiceAggregator.getInstance().get(ChannelService.class).getChannel(channelId);

        byte volume = Byte.parseByte(context.getArgs().get(0));

        DiscordServerSettings serverSettings = channel.getDiscordServer().getSettings();
        serverSettings.setVolume(volume);

        ServiceAggregator.getInstance().get(DBService.class).update(channel.getDiscordServer());

        if (channel.hasInitiatedAudioPlayer()){
            channel.playerAction().setVolume(volume);
        }

        BotUtils.sendMessage(
                new DefaultTextMessage(String.join("","Volume updated to ", "'", BotUtils.bold(String.valueOf(volume)), "'")).template(),
                context.getEvent().getChannel().asTextChannel());

        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}
