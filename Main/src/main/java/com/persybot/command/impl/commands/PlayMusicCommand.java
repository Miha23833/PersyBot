package com.persybot.command.impl.commands;

import com.persybot.command.AbstractCommand;
import com.persybot.command.CommandContext;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.audio.impl.PlayerManagerImpl;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class PlayMusicCommand extends AbstractCommand {

    public PlayMusicCommand() {
        super(1);
    }

    @Override
    public void execute(CommandContext context) {
        final TextChannel channel = context.getEvent().getChannel();
        {
            if (context.getArgs().isEmpty()) {
                channel.sendMessage("Correct usage is `!!play <youtube link>`").queue();
                return;
            }

            joinChannel(context.getEvent());

            String link = String.join(" ", context.getArgs());

            if (!isUrl(link)) {
                link = "ytsearch:" + link;
            }

            PlayerManagerImpl.getInstance().loadAndPlay(channel, link);
        }
    }

    @Override
    public String describe(CommandContext context) {
        return TEXT_COMMAND.PLAY.describeText();
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = new TextCommandValidationResult();
        if (!hasMinimumArgs(args)){
            validationResult.setInvalid(TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS, TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS.text());
        }
        return validationResult;
    }

    private void joinChannel(GuildMessageReceivedEvent event) {
        final TextChannel channel = event.getChannel();

        final GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel for this command to work").queue();
            return;
        }

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel memberChannel = memberVoiceState.getChannel();

        audioManager.openAudioConnection(memberChannel);
        channel.sendMessageFormat("Connecting to `\uD83D\uDD0A %s`", memberChannel.getName()).queue();
    }

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
