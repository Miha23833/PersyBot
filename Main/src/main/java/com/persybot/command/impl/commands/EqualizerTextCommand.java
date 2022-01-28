package com.persybot.command.impl.commands;

import com.google.common.collect.Lists;
import com.persybot.cache.service.CacheService;
import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.db.entity.EqualizerPreset;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.PAGEABLE_MESSAGE_TYPE;
import com.persybot.message.cache.PageableMessageCache;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.paginator.PageableMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.staticdata.StaticData;
import com.persybot.utils.BotUtils;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;

import java.util.List;

public class EqualizerTextCommand extends AbstractTextCommand {
    private static final float[] ROFL_BASS_BOOST = new float[] {
            1, 1, 1, 1, 0.7f,
            0.2f, n(3), n(4), n(4), n(4),
            n(3), n(3), n(2), n(2), n(2)};
    private static final float[] BASS_BOOST = new float[] {
            0.9f, 0.88f, 0.7f, 0.6f, 5,
            0,0,0,0,0,
            n(1), n(2), n(3), n(4), n(4)};

    private static final float[] LIGHT_BASS_BOOST = new float[] {
            0.7f, 0.6f, 0.6f, 0, 0,
            0,0,0,0,0,
            0 , n(2), n(2), n(2), n(3)};

    private static final float[] SOFT = new float[] {
            p(1), p(0), n(1), n(1), n(2),
            n(1), p(2), p(3), p(4), p(4),
            p(5), p(5), p(6), p(6), p(7)};

    private static final float[] POP = new float[] {
            n(1), p(2), p(3), p(4), p(5),
            p(4), n(1), n(2), n(2), n(2),
            n(4), n(4), n(4), n(4), n(4)
    };

    private static final float[] ROCK = new float[] {
            p(4), p(2), p(1), n(2), n(4),
            n(2), p(0), p(2), p(4), p(5),
            p(6), p(6), p(6), p(6), p(6)
    };

    private final ChannelService channelService;
    private final PageableMessageCache cache;

    public EqualizerTextCommand() {
        super(0);
        this.channelService = ServiceAggregator.getInstance().get(ChannelService.class);
        cache = ServiceAggregator.getInstance().get(CacheService.class).get(PageableMessageCache.class);
    }


    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> result = new TextCommandValidationResult();
        if (args == null || args.size() < 1) {
             result.setInvalid(TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS, "Please provide name of preset");
        }
        return result;
    }

    @Override
    protected boolean runBefore(TextCommandContext context) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = validateArgs(context.getArgs());
        if (!validationResult.isValid()) {
            context.getEvent().getChannel().sendMessage(validationResult.rejectText()).queue();
            return false;
        }
        return true;
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        String presetName = context.getArgs().get(0);

        EqualizerPreset preset = ServiceAggregator.getInstance().get(StaticData.class).getPreset(presetName);

        if (preset != null) {
            Channel channel = channelService.getChannel(context.getGuildId());
            if (!channel.hasInitiatedAudioPlayer()) {
                return false;
            }
            channel.getAudioPlayer().setEqualizer(preset.getBands());
            return true;
        } else {
            List<String> presetNames = ServiceAggregator.getInstance().get(StaticData.class).getEqualizerPresetNames();

            if (presetNames.size() == 0) {
                BotUtils.sendMessage("Now I don't have any equalizer preset", context.getEvent().getChannel());
                return false;
            }

            PageableMessage.Builder rsp = PageableMessage.builder();
            Lists.partition(presetNames, 8)
                    .stream()
                    .map(part -> new InfoMessage("Available equalizer presets:", String.join("\n ", part)).template())
                    .forEach(rsp::addMessage);

            BotUtils.sendPageableMessage(rsp, context.getEvent().getChannel(), PAGEABLE_MESSAGE_TYPE.EQUALIZER_LIST, cache);
            return false;
        }
    }

    @Override
    protected boolean runAfter(TextCommandContext context) {
        context.getEvent().getChannel().sendMessage("Equalizer changed").queue();
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }

    private static float p(int val) {
        return Math.min((float) (val * 0.1), 1);
    }

    private static float n(int val) {
        return Math.max(((float) (-0.025) * val), -0.25f);
    }
}
