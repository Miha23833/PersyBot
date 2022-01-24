package com.persybot.audio.audioloadreslt.impl;

import com.persybot.audio.GuildAudioPlayer;
import com.persybot.audio.TrackScheduler;
import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.persybot.audio.PlayerStateSender;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.service.MessageType;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.utils.BotUtils;
import com.persybot.utils.QueueSuccessActionTemplates;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.COMMON;
import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS;

public class DefaultAudioLoadResultHandler implements AudioLoadResultHandler, PlayerStateSender {
    private final GuildAudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;
    private final TextChannel requestingChannel;
    private final String linkToTrack;

    public DefaultAudioLoadResultHandler(GuildAudioPlayer audioPlayer, TrackScheduler trackScheduler, TextChannel requestingChannel, String linkToTrack) {
        this.audioPlayer = audioPlayer;
        this.trackScheduler = trackScheduler;
        this.requestingChannel = requestingChannel;
        this.linkToTrack = linkToTrack;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        this.trackScheduler.addTrack(new AudioTrackContextImpl(track, requestingChannel));
        runPlayerIfItIsNot();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (playlist.isSearchResult()) {
            trackLoaded(playlist.getTracks().get(0));
        } else {
            trackScheduler.addPlaylist(new AudioPlaylistContextImpl(playlist.getTracks(), requestingChannel));
            runPlayerIfItIsNot();
        }
    }

    @Override
    public void noMatches() {
        requestingChannel
                .sendMessage(new InfoMessage(
                        "Error",
                        "Could not find " + BotUtils.toHypertext("audio content", this.linkToTrack)).template())
                .queue(QueueSuccessActionTemplates.addToSelfCleaner(MessageType.ERROR));
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        PersyBotLogger.BOT_LOGGER.error(exception);

        if (exception.severity.equals(COMMON) || exception.severity.equals(SUSPICIOUS)) {
            requestingChannel
                    .sendMessage(new InfoMessage(
                            "Error",
                            "Failed to load " + BotUtils.toHypertext("track", this.linkToTrack)).template())
                    .queue(QueueSuccessActionTemplates.addToSelfCleaner(MessageType.ERROR));
        }
    }

    private void runPlayerIfItIsNot() {
        if (!audioPlayer.isPlaying()) {
            AudioTrackContext trackContext = trackScheduler.nextTrack();
            this.audioPlayer.playTrack(trackContext.getTrack());
            trackContext.getRequestingChannel()
                    .sendMessage(getPlayingTrackMessage(trackContext.getTrackPresent(), audioPlayer.isPaused()))
                    .queue(QueueSuccessActionTemplates.addToSelfCleaner(MessageType.PLAYER_NOW_PLAYING));
        }
    }
}