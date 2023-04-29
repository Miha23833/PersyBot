package com.persybot.command.impl.commands;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.utils.BotUtils;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.List;

import static com.persybot.utils.URLUtil.isPlayableLink;
import static com.persybot.utils.URLUtil.isUrl;

public class PlayMusicTextCommand extends AbstractTextCommand {
    private final DBService dbService;

    public PlayMusicTextCommand() {
        super(1);
        this.dbService = ServiceAggregator.getInstance().get(DBService.class);
    }

    @Override
    protected boolean runBefore(TextCommandContext context) {
        Member requestingMember = context.getEvent().getMember();
        if (requestingMember == null) {
            return false;
        }

        final TextChannel rspChannel = context.getEvent().getChannel().asTextChannel();
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = validateArgs(context.getArgs());

        if (!validationResult.isValid()) {
            BotUtils.sendMessage(validationResult.rejectText(), rspChannel);
            return false;
        }
        if (!BotUtils.isMemberInVoiceChannel(requestingMember)) {
            BotUtils.sendMessage("Please join to a voice channel first", rspChannel);
            return false;
        }

        if (!BotUtils.isMemberInVoiceChannel(context.getGuild().getSelfMember(),requestingMember.getVoiceState().getChannel().asVoiceChannel())
                && !BotUtils.canJoin(requestingMember, requestingMember.getVoiceState().getChannel().asVoiceChannel())) {
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
        Channel channel = ServiceAggregator.getInstance().get(ChannelService.class).getChannel(context.getGuildId());
        DiscordServer discordServer = dbService.readAssured(context.getGuildId(), DiscordServer.class);

        DiscordServerSettings audioSettings = discordServer.getSettings();

        VoiceChannel voiceChannel = context.getEvent().getMember().getVoiceState().getChannel().asVoiceChannel();
        String link = String.join(" ", context.getArgs());

        if (!BotUtils.isMemberInSameVoiceChannelAsBot(context.getEvent().getMember(), context.getGuild().getSelfMember())) {
            ServiceAggregator.getInstance().get(ChannelService.class)
                    .getChannel(context.getGuildId())
                    .voiceChannelAction().joinChannel(voiceChannel);
            BotUtils.sendMessage(new DefaultTextMessage("Connected to " + voiceChannel.getName()).template(), context.getEvent().getChannel().asTextChannel());

            if (audioSettings.getMeetAudioLink() != null && !channel.getAudioPlayer().isPlaying()) {
                channel.playerAction().playSong(audioSettings.getMeetAudioLink(), context.getEvent().getChannel().asTextChannel());
            }
        }

        channel.playerAction().playSong(link, context.getEvent().getChannel().asTextChannel());
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return "Plays music. To use write '''<prefix>play <link or name of sound>'''";
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = new TextCommandValidationResult();
        if (!hasMinimumArgs(args)){
            validationResult.setInvalid(TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS, "Correct usage is `play <link or title of playing track>`");
            return validationResult;
        }

        String link = String.join(" ", args);
        if (isUrl(link) && !isPlayableLink(link)) {
            validationResult.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, "I cannot play this url");
        }
        return validationResult;
    }
}
