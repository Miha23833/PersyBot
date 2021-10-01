package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.impl.TrackSchedulerImpl;
import com.persybot.logger.impl.PersyBotLogger;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

public class DefaultAudioLoadResultHandler implements AudioLoadResultHandler {
    private final TrackSchedulerImpl scheduler;
    private final boolean isSingleTrack;
    private final TextChannel requestingChannel;

    public DefaultAudioLoadResultHandler(TrackSchedulerImpl scheduler, TextChannel requestingChannel, boolean isSingleTrack) {
        this.isSingleTrack = isSingleTrack;
        this.scheduler = scheduler;
        this.requestingChannel = requestingChannel;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        this.scheduler.addTrack(new AudioTrackContextImpl(track, requestingChannel));
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (isSingleTrack) {
            scheduler.addTrack(new AudioTrackContextImpl(playlist.getTracks().get(0), requestingChannel));
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
        requestingChannel.sendMessage("Failed to load track.").queue();
    }
}
