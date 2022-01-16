package com.persybot.command.impl.commands;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.service.impl.ServiceAggregatorImpl;
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

    public EqualizerTextCommand() {
        super(0);
        this.channelService = ServiceAggregatorImpl.getInstance().getService(ChannelService.class);
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

        if (presetName.equalsIgnoreCase("off")) {
            channelService.getChannel(context.getGuildId()).getAudioPlayer().removeEqualizer();
            return true;
        }

        float[] preset = null;

        if (presetName.equalsIgnoreCase("rofl")) {
            preset = ROFL_BASS_BOOST;
        }
        else if (presetName.equalsIgnoreCase("bassboost")) {
            preset = BASS_BOOST;
        }
        else if (presetName.equalsIgnoreCase("lightbb")) {
            preset = LIGHT_BASS_BOOST;
        }
        else if (presetName.equalsIgnoreCase("soft")) {
            preset = SOFT;
        }
        else if (presetName.equalsIgnoreCase("pop")) {
            preset = POP;
        }
        else if (presetName.equalsIgnoreCase("rock")) {
            preset = ROCK;
        }

        if (preset != null) {
            channelService.getChannel(context.getGuildId()).getAudioPlayer().setEqualizer(preset);
            return true;
        } else {
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
