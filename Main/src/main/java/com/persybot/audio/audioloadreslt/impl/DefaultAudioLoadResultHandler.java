package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.impl.TrackSchedulerImpl;
import com.persybot.callback.consumer.MessageSendSuccess;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.service.MessageType;
import com.persybot.message.template.impl.InfoMessage;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

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
            scheduler.addPlaylist(new AudioPlaylistContextImpl(playlist.getTracks(), requestingChannel));
        }
    }

    @Override
    public void noMatches() {
        requestingChannel.sendMessage("Cannot find track.").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        PersyBotLogger.BOT_LOGGER.error(exception);
        requestingChannel.sendMessage(new InfoMessage("Error", "Failed to load track.").template()).queue(x -> new MessageSendSuccess<>(MessageType.ERROR, x).accept(x));
    }
}