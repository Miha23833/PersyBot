package com.persybot.command.impl.commands;

import com.persybot.command.AbstractCommand;
import com.persybot.command.CommandContext;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.List;

public class LeaveChannelCommand extends AbstractCommand {
    public LeaveChannelCommand() {
        super(0);
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        return new TextCommandValidationResult();
    }

    @Override
    public void execute(CommandContext context) {
        GuildVoiceState connectedChannelState = context.getEvent().getGuild().getSelfMember().getVoiceState();

        if (connectedChannelState == null) {
            context.getEvent().getChannel().sendMessage("I am not connected to a voice channel!").queue();
            return;
        }

        VoiceChannel connectedChannel = connectedChannelState.getChannel();
        // Checks if the bot is connected to a voice channel.
        if(connectedChannel == null) {
            // Get slightly fed up at the user.
            context.getEvent().getChannel().sendMessage("I am not connected to a voice channel!").queue();
            return;
        }
        // Disconnect from the channel.
        context.getEvent().getGuild().getAudioManager().closeAudioConnection();
        // Notify the user.
        context.getEvent().getChannel().sendMessage("Disconnected from the voice channel!").queue();
    }

    @Override
    public String describe(CommandContext context) {
        return null;
    }
}
