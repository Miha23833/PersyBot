package com.persybot.command;

import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.validation.ValidationResult;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public abstract class AbstractCommand implements Command{
    private final int minArgs;

    protected AbstractCommand(int minArgs) {
        this.minArgs = minArgs;
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
}
