package com.persybot.audio.loader;

import com.persybot.audio.TrackScheduler;
import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.persybot.audio.audioloadreslt.impl.AudioPlaylistContextImpl;
import com.persybot.audio.audioloadreslt.impl.AudioTrackContextImpl;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.service.MessageType;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.utils.QueueSuccessActionTemplates;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import static com.persybot.utils.BotUtils.toHypertext;
import static com.persybot.utils.URLUtil.isUrl;
import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.COMMON;
import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS;

public class AudioLoaderImpl implements AudioLoader {
    private final AudioPlayerManager manager;
    private final TrackScheduler scheduler;

    public AudioLoaderImpl(AudioPlayerManager manager, TrackScheduler scheduler) {
        this.manager = manager;
        this.scheduler = scheduler;
    }

    @Override
    public void load(String req, TextChannel requestingChannel) {
        this.manager.loadItemOrdered(this, req, new ContextWrappingAudioLoadResultHandler(this, requestingChannel, req));
    }

    private void onTrackLoaded(AudioTrackContext context) {
        this.scheduler.addTrack(context);
    }

    private void onPlaylistLoaded(AudioPlaylistContext context) {
        this.scheduler.addPlaylist(context);
    }

    private static class ContextWrappingAudioLoadResultHandler implements AudioLoadResultHandler {

        private final AudioLoaderImpl audioLoader;
        private final TextChannel requestingChannel;
        private final String search;

        public ContextWrappingAudioLoadResultHandler (AudioLoaderImpl audioLoader, TextChannel requestingChannel, String search) {
            this.audioLoader = audioLoader;
            this.requestingChannel = requestingChannel;
            this.search = search;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            this.audioLoader.onTrackLoaded(new AudioTrackContextImpl(track, requestingChannel));
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            if (playlist.getTracks().isEmpty()) {
                return;
            }
            if (playlist.isSearchResult()) {
                this.audioLoader.onTrackLoaded(new AudioTrackContextImpl(playlist.getTracks().get(0), requestingChannel));
            } else {
                this.audioLoader.onPlaylistLoaded(new AudioPlaylistContextImpl(playlist.getTracks(), requestingChannel));
            }
        }

            @Override
            public void noMatches() {
                requestingChannel
                        .sendMessage(new InfoMessage(
                                "Error",
                                "Could not find " + (isUrl(this.search) ? toHypertext("audio content", this.search) : this.search)).template())
                        .queue(QueueSuccessActionTemplates.addToSelfCleaner(MessageType.ERROR));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                PersyBotLogger.BOT_LOGGER.error(exception);

                if (exception.severity.equals(COMMON) || exception.severity.equals(SUSPICIOUS)) {
                    requestingChannel
                            .sendMessage(new InfoMessage(
                                    "Error",
                                    "Failed to load " + (isUrl(this.search) ? toHypertext("audio content", this.search) : this.search)).template())
                            .queue(QueueSuccessActionTemplates.addToSelfCleaner(MessageType.ERROR));
                }
            }
        }
}
