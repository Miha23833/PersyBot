package com.persybot.audio.impl;

import com.persybot.audio.PlayerStateSender;
import com.persybot.audio.TrackScheduler;
import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.persybot.message.service.MessageType;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.utils.QueueSuccessActionTemplates;
import com.persybot.utils.URLUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SynchronizedTrackScheduler implements TrackScheduler, PlayerStateSender {
    private final AudioPlayer player;
    private final List<AudioTrackContext> trackQueue;
    private final int maxQueueSize;

    public SynchronizedTrackScheduler(AudioPlayer player, int maxQueueSize) {
        this.player = player;
        this.maxQueueSize = maxQueueSize;
        trackQueue = new LinkedList<>();
    }

    @Override
    public void addTrack(AudioTrackContext track) {
        if (trackQueue.size() >= maxQueueSize) {
            track.getRequestingChannel().
                    sendMessage(new InfoMessage("Cannot add track", "Queue is full (" + maxQueueSize +" max)").template()).queue();
            return;
        }
        if (this.player.getPlayingTrack() == null) {
            this.player.playTrack(track.getTrack());

            track.getRequestingChannel()
                    .sendMessage(getPlayingTrackMessage(track.getTrackPresent(), player.isPaused()))
                    .queue(QueueSuccessActionTemplates.addToSelfCleaner(MessageType.PLAYER_NOW_PLAYING));
        }
        else {
            this.trackQueue.add(track);
            if (URLUtil.isYoutubeDomain(track.getTrack().getInfo().uri)) {
                track.getRequestingChannel().
                        sendMessage(new InfoMessage("Queued track: ", track.getTrack().getInfo().title).template()).queue();
            } else {
                track.getRequestingChannel().
                        sendMessage(new InfoMessage("Queued track: ", track.getTrack().getInfo().author + " - " + track.getTrack().getInfo().title).template()).queue();
            }
        }
    }

    @Override
    public void addPlaylist(AudioPlaylistContext playlist) {
        List<AudioTrackContext> tracks = playlist.getTracks();
        if (trackQueue.size() >= maxQueueSize) {
            tracks.get(0).getRequestingChannel().
                    sendMessage(new InfoMessage("Cannot add track", "Queue is full (" + maxQueueSize +" max)").template()).queue();
            return;
        } else if (trackQueue.size() + tracks.size() > maxQueueSize) {
            tracks = tracks.subList(0, maxQueueSize - trackQueue.size());
        }
        boolean startFirst = this.player.getPlayingTrack() == null;
        if (startFirst) {
            addTrack(tracks.remove(0));
        }
        this.trackQueue.addAll(tracks);
        if (!tracks.isEmpty()) {
            tracks.get(0).getRequestingChannel().sendMessage(getQueuedTrackMessage(tracks)).queue();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.trackQueue.isEmpty();
    }

    @Override
    public AudioTrackContext skipMultiple(int countOfSkips) {
        if (isEmpty() || this.trackQueue.size() < countOfSkips) {
            this.trackQueue.clear();
            return null;
        }

        for (int i = 1; i < countOfSkips; i++) {
            this.trackQueue.remove(0);
        }
        return nextTrack();
    }

    @Override
    public AudioTrackContext nextTrack() {
        if (isEmpty()) {
            return null;
        }

        return this.trackQueue.remove(0);
    }

    @Override
    public void clear() {
        this.trackQueue.clear();
    }

    @Override
    public void shuffle() {
        Collections.shuffle(this.trackQueue);
    }

    @Override
    public List<String> queuedTracksTitles() {
        return this.trackQueue.stream().map(AudioTrackContext::getTrackPresent).collect(Collectors.toList());
    }

    private MessageCreateData getQueuedTrackMessage(List<AudioTrackContext> tracks) {
        StringBuilder queuedTracksRsp = new StringBuilder();

        int trackInfoRspLineLimit = Math.min(tracks.size(), 8);

        for (int i = 0; i < trackInfoRspLineLimit; i++) {
            AudioTrackContext trackContext = tracks.get(i);
            queuedTracksRsp.append(trackContext.getTrackPresent()).append("\n");
        }

        if (tracks.size() > trackInfoRspLineLimit) {
            queuedTracksRsp.append("And ")
                    .append(tracks.size() - trackInfoRspLineLimit).append(" more");
        }
        return new InfoMessage("Queued tracks:", queuedTracksRsp.toString()).template();
    }
}
