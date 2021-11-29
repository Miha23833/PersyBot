package com.persybot.command.impl.commands;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.utils.BotUtils;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
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
    protected boolean runBefore(TextCommandContext context) {
        Member requestingMember = context.getEvent().getMember();
        if (requestingMember == null) {
            return false;
        }

        final TextChannel rspChannel = context.getEvent().getChannel();
        if (TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS.equals(validateArgs(context.getArgs()).getRejectReason())) {
            BotUtils.sendMessage("Correct usage is `play <youtube link>`", rspChannel);
            return false;
        }
        if (!BotUtils.isMemberInVoiceChannel(requestingMember)) {
            BotUtils.sendMessage("Please join to a voice channel first", rspChannel);
            return false;
        }

        if (!BotUtils.isMemberInVoiceChannel(context.getGuild().getSelfMember(),requestingMember.getVoiceState().getChannel())
                && !BotUtils.canJoin(requestingMember, requestingMember.getVoiceState().getChannel())) {
            BotUtils.sendMessage("I cannot connect to your voice channel", rspChannel);
            return false;
        }

        if (!BotUtils.canSpeak(context.getGuild().getSelfMember())) {
            BotUtils.sendMessage("I cannot speak in your voice channel", rspChannel);
            return false;
        }
        return true;
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        Channel channel = ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(context.getGuildId());
        AudioManager audioManager = context.getEvent().getGuild().getAudioManager();

        if (audioManager.getSendingHandler() == null) {
            audioManager.setSendingHandler(channel.getAudioPlayer().getSendHandler());
        }

        VoiceChannel voiceChannel = context.getEvent().getMember().getVoiceState().getChannel();

        if (!BotUtils.isMemberInSameVoiceChannelAsBot(context.getEvent().getMember(), context.getGuild().getSelfMember())) {
            ServiceAggregatorImpl.getInstance().getService(ChannelService.class)
                    .getChannel(context.getGuildId())
                    .voiceChannelAction().joinChannel(voiceChannel);

            BotUtils.sendMessage("Connected to " + voiceChannel.getName(), context.getEvent().getChannel());
        }

        String link = String.join(" ", context.getArgs());
        channel.playerAction().playSong(link, context.getEvent().getChannel());


        return true;
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
