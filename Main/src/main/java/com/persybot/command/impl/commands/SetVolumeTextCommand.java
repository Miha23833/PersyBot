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

public class SetVolumeTextCommand extends AbstractTextCommand {

    private final ServiceAggregatorImpl serviceAggregator;

    public SetVolumeTextCommand() {
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
    public void execute(TextCommandContext context) {
        long channelId = context.getEvent().getGuild().getIdLong();
        int volume = Integer.parseInt(context.getArgs().get(0));

        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = validateArgs(context.getArgs());

        if (!validationResult.isValid()) {
            context.getEvent().getChannel().sendMessage(new DefaultTextMessage(validationResult.rejectText()).template()).queue();
            return;
        }
        Channel channel = ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(channelId);

        DiscordServerSettings serverSettings = channel.getServerSettings();
        serverSettings.setVolume(Integer.parseInt(context.getArgs().get(0)));

        ServiceAggregatorImpl.getInstance().getService(DBService.class).update(serverSettings);

        context.getEvent().getChannel().sendMessage(new DefaultTextMessage(String.join("","Volume updated to ", "'", String.valueOf(volume), "'")).template()).queue();

        ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(channelId)
                .playerAction().setVolume(Integer.parseInt(context.getArgs().get(0)));
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }
}
