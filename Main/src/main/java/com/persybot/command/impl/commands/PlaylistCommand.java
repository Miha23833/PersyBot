package com.persybot.command.impl.commands;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.db.entity.PlayList;
import com.persybot.db.service.DBService;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.utils.BotUtils;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PlaylistCommand extends AbstractTextCommand {
    private final int maxPlaylistNameSize;

    public PlaylistCommand(int maxPlaylistNameSize) {
        super(1);
        this.maxPlaylistNameSize = maxPlaylistNameSize;
    }

    @Override
    protected boolean runBefore(TextCommandContext context) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = validateArgs(context.getArgs());

        if (!validationResult.isValid()) {
            BotUtils.sendMessage(new DefaultTextMessage(validationResult.rejectText()).template(), context.getEvent().getChannel());
            return false;
        }

        if (context.getArgs().size() > 1) {
            String playListName = context.getArgs().get(0);
            if (playListName == null || playListName.isBlank()) {
                sendInvalidPlaylistNameMsg(context.getEvent().getChannel());
                return false;
            }

            if (context.getArgs().size() > 2) {
                String playlistLink = context.getArgs().get(1);
                try {
                    new URL(playlistLink);
                } catch (MalformedURLException e) {
                    sendInvalidPlaylistUrlMsg(context.getEvent().getChannel());
                    return false;
                }
            }
        }

        Member requestingMember = context.getEvent().getMember();
        if (requestingMember == null) {
            return false;
        }

        final TextChannel rspChannel = context.getEvent().getChannel();
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
        if (context.getArgs().size() == 1) {
            String playListName = context.getArgs().get(0);
                playPlaylist(playListName, context.getGuildId(), context.getEvent().getChannel(), context);
        }
        else if (context.getArgs().size() > 1) {
            String playListName = context.getArgs().get(0);
            String playlistLink = context.getArgs().get(1);
            savePlaylist(playListName, context.getGuildId(), playlistLink, context.getEvent().getChannel());
        }
        return true;
    }

    @Override
    public String describe(TextCommandContext context) {
        return null;
    }

    @Override
    protected ValidationResult<TEXT_COMMAND_REJECT_REASON> validateArgs(List<String> args) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> result = new TextCommandValidationResult();

        if (!hasMinimumArgs(args)) {
            result.setInvalid(TEXT_COMMAND_REJECT_REASON.NOT_ENOUGH_ARGS, "Correct usage: playlist [name] [link]");
        }

        return result;
    }

    private void savePlaylist(String playListName, Long guildId, String playlistLink, TextChannel rspChanel) {
        PlayList playlist = ServiceAggregatorImpl.getInstance().getService(DBService.class).getPlaylistByName(playListName, guildId);

        if (playlist == null) {
            playlist = new PlayList(guildId, playListName, playlistLink);
            ServiceAggregatorImpl.getInstance().getService(DBService.class).savePlayList(playlist);
        } else {
            playlist.setUrl(playlistLink);
            ServiceAggregatorImpl.getInstance().getService(DBService.class).updatePlayList(playlist);
        }

        rspChanel.sendMessage(new InfoMessage(null, "Playlist was saved").template()).queue();
    }

    private void playPlaylist(String playListName, Long guildId, TextChannel rspChannel, TextCommandContext context) {
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
            BotUtils.sendMessage(new DefaultTextMessage("Connected to " + voiceChannel.getName()).template(), context.getEvent().getChannel());
        }
        PlayList playList = ServiceAggregatorImpl.getInstance().getService(DBService.class).getPlaylistByName(playListName, guildId);

        ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(guildId).playerAction().playSong(playList.getUrl(), rspChannel);
    }

    private void sendInvalidPlaylistNameMsg(TextChannel channel) {
        channel.sendMessage(new DefaultTextMessage("Please provide correct playlist name.").template()).queue();
    }

    private void sendInvalidPlaylistUrlMsg(TextChannel channel) {
        channel.sendMessage(new DefaultTextMessage("Please provide correct playlist link.").template()).queue();
    }

    private void sendPlaylistNameTooLong(TextChannel channel) {
        channel.sendMessage(new DefaultTextMessage("Max length of playlist name is " + maxPlaylistNameSize).template()).queue();
    }
}
