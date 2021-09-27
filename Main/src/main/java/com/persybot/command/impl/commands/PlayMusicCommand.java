package com.persybot.command.impl.commands;

import com.persybot.audio.impl.PlayerManagerServiceImpl;
import com.persybot.command.AbstractCommand;
import com.persybot.command.CommandContext;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class PlayMusicCommand extends AbstractCommand {

    public PlayMusicCommand() {
        super(1);
    }

    @Override
    public void execute(CommandContext context) {
        final TextChannel channel = context.getEvent().getChannel();
        if (context.getArgs().isEmpty()) {
            channel.sendMessage("Correct usage is  `play <youtube link>`").queue();
            return;
        }

        GuildVoiceState voiceState = Objects.requireNonNull(context.getEvent().getMember()).getVoiceState();
        AudioManager audioManager = context.getEvent().getGuild().getAudioManager();

        joinChannel(channel, voiceState, audioManager);

        String link = String.join(" ", context.getArgs());

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManagerServiceImpl.getInstance().loadAndPlay(channel, link);
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

    private void joinChannel(TextChannel textChannel, GuildVoiceState voiceState, AudioManager audioManager) {
        if (!isExecutorInVoiceChannel(voiceState)) {
            textChannel.sendMessage("You need to be in a voice channel for this command to work").queue();
            return;
        }

        final VoiceChannel memberChannel = voiceState.getChannel();

        audioManager.openAudioConnection(memberChannel);

        textChannel.sendMessageFormat("Connecting to `\uD83D\uDD0A %s`", Objects.requireNonNull(memberChannel).getName()).queue();
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
