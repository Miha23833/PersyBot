package com.persybot.command.impl.commands;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.impl.PlayerMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;
import java.util.Objects;

public class PlayMusicTextCommand extends AbstractTextCommand {

    public PlayMusicTextCommand() {
        super(1);
    }

    @Override
    public void execute(TextCommandContext context) {
        final TextChannel rspChannel = context.getEvent().getChannel();
        if (TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS.equals(validateArgs(context.getArgs()).getRejectReason())) {
            rspChannel.sendMessage("Correct usage is `play <youtube link>`").queue();
            return;
        }

        Channel channel = ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(context.getGuildId());
        AudioManager audioManager = context.getEvent().getGuild().getAudioManager();
        if (audioManager.getSendingHandler() == null) {
           audioManager.setSendingHandler(channel.getAudioPlayer().getSendHandler());
        }

        GuildVoiceState voiceState = Objects.requireNonNull(context.getEvent().getMember()).getVoiceState();
        if (voiceState == null) {
            rspChannel.sendMessage("Pleas join to a voice first").queue();
            return;
        }

        ServiceAggregatorImpl.getInstance().getService(ChannelService.class)
                .getChannel(context.getGuildId())
                .voiceChannelAction().joinChannel(voiceState.getChannel());

        String link = String.join(" ", context.getArgs());
        channel.playerAction().playSong(link, context.getEvent().getChannel());


        context.getEvent().getChannel().sendMessage(new PlayerMessage("currentTrack", false, false).getMessage()).queue(x -> x.getIdLong());
    }

    @Override
    public String describe(TextCommandContext context) {
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
}
