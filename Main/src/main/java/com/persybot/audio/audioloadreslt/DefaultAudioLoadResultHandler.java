package com.persybot.audio.audioloadreslt;

import com.persybot.audio.cache.impl.AudioCache;
import com.persybot.audio.impl.GuildMusicManager;
import com.persybot.cache.Cache;
import com.persybot.logger.impl.PersyBotLogger;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.internal.interactions.ButtonImpl;

import java.util.List;

public class DefaultAudioLoadResultHandler implements AudioLoadResultHandler {
    private final Cache<String, AudioTrack> cache = AudioCache.getInstance();

    private final GuildMusicManager musicManager;
    private final TextChannel rspChannel;

    public DefaultAudioLoadResultHandler(GuildMusicManager musicManager, TextChannel rspChannel) {
        this.musicManager = musicManager;
        this.rspChannel = rspChannel;
    }


    @Override
    public void trackLoaded(AudioTrack track) {
        cache.addObject(track);
        musicManager.scheduler.queue(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        final List<AudioTrack> tracks = playlist.getTracks();

        for (final AudioTrack track: tracks) {
            cache.addObject(track);
            musicManager.scheduler.queue(track);
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
