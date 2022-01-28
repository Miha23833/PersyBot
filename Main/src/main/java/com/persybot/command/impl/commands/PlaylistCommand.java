package com.persybot.command.impl.commands;

import com.google.common.collect.Lists;
import com.persybot.cache.service.CacheService;
import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.command.AbstractTextCommand;
import com.persybot.command.TextCommandContext;
import com.persybot.db.entity.PlayList;
import com.persybot.db.entity.ServerAudioSettings;
import com.persybot.db.service.DBService;
import com.persybot.enums.TEXT_COMMAND_REJECT_REASON;
import com.persybot.message.PAGEABLE_MESSAGE_TYPE;
import com.persybot.message.cache.PageableMessageCache;
import com.persybot.message.template.impl.DefaultTextMessage;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.paginator.PageableMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.utils.BotUtils;
import com.persybot.validation.ValidationResult;
import com.persybot.validation.impl.TextCommandValidationResult;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.persybot.utils.URLUtil.isPlayableLink;
import static com.persybot.utils.URLUtil.isUrl;

public class PlaylistCommand extends AbstractTextCommand {
    private final DBService dbService;
    private final ServiceAggregator serviceAggregator;
    private final PageableMessageCache pageableMessageCache;

    private final int maxPlaylistNameSize;
    private static final String SHOW_PLAYLIST_LIST_KEYWORD = "list";

    public PlaylistCommand(int maxPlaylistNameSize) {
        super(1);
        this.maxPlaylistNameSize = maxPlaylistNameSize;

        this.serviceAggregator = ServiceAggregator.getInstance();
        this.dbService = this.serviceAggregator.get(DBService.class);
        this.pageableMessageCache = serviceAggregator.get(CacheService.class).get(PageableMessageCache.class);
    }

    @Override
    protected boolean runBefore(TextCommandContext context) {
        ValidationResult<TEXT_COMMAND_REJECT_REASON> validationResult = validateArgs(context.getArgs());

        if (!validationResult.isValid()) {
            BotUtils.sendMessage(new DefaultTextMessage(validationResult.rejectText()).template(), context.getEvent().getChannel());
            return false;
        }

        Member requestingMember = context.getEvent().getMember();

        if (requestingMember == null) {
            return false;
        }

        if (context.getArgs().get(0).equals(SHOW_PLAYLIST_LIST_KEYWORD)) {
            return true;
        }

        final TextChannel rspChannel = context.getEvent().getChannel();

        if (context.getArgs().size() == 1) {
            if (!BotUtils.isMemberInVoiceChannel(requestingMember)) {
                BotUtils.sendMessage("Please join to a voice channel first", rspChannel);
                return false;
            }

            if (!BotUtils.isMemberInVoiceChannel(context.getGuild().getSelfMember(), requestingMember.getVoiceState().getChannel())
                    && !BotUtils.canJoin(requestingMember, requestingMember.getVoiceState().getChannel())) {
                BotUtils.sendMessage("I cannot connect to your voice channel", rspChannel);
                return false;
            }

            if (!BotUtils.canSpeak(context.getGuild().getSelfMember())) {
                BotUtils.sendMessage("I cannot speak in your voice channel", rspChannel);
                return false;
            }

            String playListName = context.getArgs().get(0);
            if (playListName == null || playListName.isBlank()) {
                BotUtils.sendMessage("Please provide correct playlist name", rspChannel);
                return false;
            }
        } else if (context.getArgs().size() > 2) {
            String playlistName = context.getArgs().get(0);
            if (playlistName.length() > this.maxPlaylistNameSize) {
                BotUtils.sendMessage("Max length of playlist name is " + maxPlaylistNameSize, rspChannel);
            }

            String playlistLink = context.getArgs().get(1);
            try {
                new URL(playlistLink);
            } catch (MalformedURLException e) {
                BotUtils.sendMessage("Please provide correct playlist link", rspChannel);
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean runCommand(TextCommandContext context) {
        if (context.getArgs().size() == 1) {
            if (context.getArgs().get(0).equals(SHOW_PLAYLIST_LIST_KEYWORD)) {
                Map<Long, PlayList> playlists = getPlaylists(context.getGuildId());
                if (playlists.isEmpty()) {
                    BotUtils.sendMessage("There is no playlists", context.getEvent().getChannel());
                }
                sendListOfPlaylists(context.getGuildId(), context.getEvent().getChannel());
                return true;
            } else {
                String playListName = context.getArgs().get(0);

                if (context.getEvent().getMember() == null) return false;

                if (!BotUtils.isMemberInSameVoiceChannelAsBot(context.getEvent().getMember(), context.getGuild().getSelfMember())) {
                    Optional<ServerAudioSettings> audioSettings = dbService.getServerAudioSettings(context.getGuildId());
                    audioSettings.ifPresent(as -> {
                        Channel channel = this.serviceAggregator.get(ChannelService.class).getChannel(context.getGuildId());
                        if (as.getMeetAudioLink() != null && !channel.hasInitiatedAudioPlayer()) {
                            channel.playerAction().playSong(as.getMeetAudioLink(), context.getEvent().getChannel());
                        }
                    });
                }
                playPlaylist(playListName, context.getGuildId(), context.getEvent().getChannel(), context);
            }
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
            return result;
        }

        if (args.size() > 1 && args.get(1) != null) {
            if (!isUrl(args.get(1)) && !isPlayableLink(args.get(1))) {
                result.setInvalid(TEXT_COMMAND_REJECT_REASON.WRONG_VALUE, "Second argument must be playable link");
                return result;
            }
        }

        return result;
    }

    private void savePlaylist(String playListName, Long guildId, String playlistLink, TextChannel rspChanel) {
        PlayList playlist = this.serviceAggregator.get(DBService.class).getPlaylistByName(playListName, guildId).orElse(null);

        if (playlist == null) {
            playlist = new PlayList(guildId, playListName, playlistLink);
            this.serviceAggregator.get(DBService.class).savePlayList(playlist);
        } else {
            playlist.setUrl(playlistLink);
            this.serviceAggregator.get(DBService.class).updatePlayList(playlist);
        }

        rspChanel.sendMessage(new InfoMessage(null, "Playlist was saved").template()).queue();
    }

    private void playPlaylist(String playlistName, Long guildId, TextChannel rspChannel, TextCommandContext context) {
        PlayList playList = this.serviceAggregator.get(DBService.class).getPlaylistByName(playlistName, guildId).orElse(null);

        if (playList == null) {
            BotUtils.sendMessage("Playlist \"" + playlistName + "\" not found", rspChannel);
            return;
        }

        VoiceChannel voiceChannel = context.getEvent().getMember().getVoiceState().getChannel();

        if (!BotUtils.isMemberInSameVoiceChannelAsBot(context.getEvent().getMember(), context.getGuild().getSelfMember())) {
            this.serviceAggregator.get(ChannelService.class)
                    .getChannel(context.getGuildId())
                    .voiceChannelAction().joinChannel(voiceChannel);
            BotUtils.sendMessage(new DefaultTextMessage("Connected to " + voiceChannel.getName()).template(), context.getEvent().getChannel());
        }

        this.serviceAggregator.get(ChannelService.class).getChannel(guildId).playerAction().playSong(playList.getUrl(), rspChannel);
    }

    private void sendListOfPlaylists(long serverId, TextChannel rspChannel) {
        Map<Long, PlayList> playlists = getPlaylists(serverId);
        List<String> data = new LinkedList<>();

        for (PlayList playlist: playlists.values()) {
            if (isUrl(playlist.getUrl())) {
                data.add(BotUtils.toHypertext(playlist.getName(), playlist.getUrl()));
            }
            else {
                data.add(playlist.getName() + " - " + playlist.getUrl());
            }
        }

        PageableMessage.Builder rsp = PageableMessage.builder();
        Lists.partition(data, 8)
                .stream()
                .map(part -> new InfoMessage("Available playlists:", String.join("\n ", part)).template())
                .forEach(rsp::addMessage);

        BotUtils.sendPageableMessage(rsp, rspChannel, PAGEABLE_MESSAGE_TYPE.PLAYLISTS, pageableMessageCache);
    }

    private Map<Long, PlayList> getPlaylists(long serverId) {
        return this.serviceAggregator.get(DBService.class).getAllPlaylistForServer(serverId).orElse(new HashMap<>());
    }
}
