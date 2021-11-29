package com.persybot.command;

import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.validation.ValidationResult;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractTextCommand implements TextCommand {
    private final int minArgs;

    private final List<Function<TextCommandContext, Boolean>> executingSequence;

    protected AbstractTextCommand(int minArgs) {
        this.minArgs = minArgs;
        this.executingSequence = Arrays.asList(
                this::runBefore,
                this::runCommand,
                this::runAfter);
    }

    @Override
    public void execute(TextCommandContext context) {
        try {
            boolean canContinue = true;
            for (Function<TextCommandContext, Boolean> step: executingSequence) {
                if (!canContinue) {
                    return;
                }
                canContinue = step.apply(context);
            }
        } catch (Throwable e) {
            onException(e);
        }
    }

    protected boolean hasMinimumArgs(List<String> args) {
        return args.size() >= minArgs;
    }

    protected abstract ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args);

    protected boolean isExecutorInVoiceChannel(GuildVoiceState memberVoiceState) {
        return memberVoiceState != null && memberVoiceState.inVoiceChannel() && memberVoiceState.getChannel() != null;
    }

    protected boolean isExecutorAndBotAreInSameVoiceChannel(GuildVoiceState memberVoiceState, AudioManager audioManager) {
        return (isExecutorInVoiceChannel(memberVoiceState) && audioManager.isConnected());
    }

    protected boolean runBefore(TextCommandContext context){
        return true;
    }
    protected boolean runCommand(TextCommandContext context){
        return true;
    }
    protected boolean runAfter(TextCommandContext context){
        return true;
    }
    protected void onException(Throwable e) {
        PersyBotLogger.BOT_LOGGER.error(e);
    }


}
