package com.persybot.audio.audioloadreslt;

import com.persybot.audio.impl.TrackScheduler;
import com.persybot.logger.impl.PersyBotLogger;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class DefaultAudioLoadResultHandler implements AudioLoadResultHandler {
    private final TrackScheduler scheduler;
    private final TextChannel rspChannel;

    public DefaultAudioLoadResultHandler(TrackScheduler scheduler, TextChannel rspChannel) {
        this.scheduler = scheduler;
        this.rspChannel = rspChannel;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        this.scheduler.queue(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        final List<AudioTrack> tracks = playlist.getTracks();

        for (final AudioTrack track: tracks) {
            this.scheduler.queue(track);
        }
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException exception) {
        PersyBotLogger.BOT_LOGGER.error(exception);
    }
}
