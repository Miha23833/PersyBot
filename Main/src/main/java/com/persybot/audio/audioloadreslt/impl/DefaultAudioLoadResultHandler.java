package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.persybot.audio.impl.TrackSchedulerImpl;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.service.MessageType;
import com.persybot.message.service.SelfFloodController;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.COMMON;
import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS;

public class DefaultAudioLoadResultHandler implements AudioLoadResultHandler {
    private final TrackSchedulerImpl scheduler;
    private final TextChannel requestingChannel;

    public DefaultAudioLoadResultHandler(TrackSchedulerImpl scheduler, TextChannel requestingChannel) {
        this.scheduler = scheduler;
        this.requestingChannel = requestingChannel;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        this.scheduler.addTrack(new AudioTrackContextImpl(track, requestingChannel));
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.isSearchResult()) {
            AudioTrack track = playlist.getTracks().get(0);
            trackLoaded(track);
        } else {
            List<AudioTrackContext> tracks = playlist.getTracks().stream().map(x -> new AudioTrackContextImpl(x, requestingChannel)).collect(Collectors.toList());
            scheduler.addPlaylist(new AudioPlaylistContextImpl(tracks, requestingChannel));
        }
    }

    @Override
    public void noMatches() {
        requestingChannel.sendMessage("Cannot find track").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        PersyBotLogger.BOT_LOGGER.error(exception);

        if (exception.severity.equals(COMMON) || exception.severity.equals(SUSPICIOUS)) {
            requestingChannel.sendMessage(new InfoMessage("Error", "Failed to load track").template())
                    .queue(x -> ServiceAggregator.getInstance().get(SelfFloodController.class)
                            .addMessage(MessageType.ERROR, x.getTextChannel().getIdLong(), x.getIdLong()));
        }
    }
}