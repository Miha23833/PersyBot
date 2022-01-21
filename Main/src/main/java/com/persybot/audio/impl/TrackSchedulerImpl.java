package com.persybot.audio.impl;

import com.persybot.audio.TrackScheduler;
import com.persybot.audio.audioloadreslt.AudioPlaylistContext;
import com.persybot.audio.audioloadreslt.AudioTrackContext;
import com.persybot.audio.audioloadreslt.impl.AudioTrackContextImpl;
import com.persybot.message.PLAYER_BUTTON;
import com.persybot.message.service.MessageType;
import com.persybot.message.service.SelfFloodController;
import com.persybot.message.template.impl.InfoMessage;
import com.persybot.service.impl.ServiceAggregator;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class TrackSchedulerImpl extends AudioEventAdapter implements TrackScheduler {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrackContext> queue;

    private AudioTrack repeatingTrack = null;

    public TrackSchedulerImpl(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void addTrack(AudioTrackContext context) {
        if (!this.player.startTrack(context.getTrack(), true)) {
            context.getRequestingChannel().sendMessage("Queued track: " + context.getTrack().getInfo().title).queue();
            this.queue.offer(context);
        } else {
            context.getRequestingChannel().sendMessage(getPlayingTrackMessage(context.getTrackPresent()))
                    .queue(x -> ServiceAggregator.getInstance().get(SelfFloodController.class)
                            .addMessage(MessageType.PLAYER_NOW_PLAYING, x.getTextChannel().getIdLong(), x.getIdLong()));
        }
    }

    @Override
    public void addPlaylist(AudioPlaylistContext playlistContext) {
        boolean firstPlaying = queue.isEmpty();

        if (firstPlaying) {
            addTrack(new AudioTrackContextImpl(playlistContext.getTracks().get(0).getTrack(), playlistContext.getRequestingChannel()));

            playlistContext.getTracks().stream().skip(1).forEach(track -> queue.add(new AudioTrackContextImpl(track.getTrack(), playlistContext.getRequestingChannel())));

        } else {
            playlistContext.getTracks().forEach(track -> queue.add(new AudioTrackContextImpl(track.getTrack(), playlistContext.getRequestingChannel())));
        }


        StringBuilder queuedTracksRsp = new StringBuilder();
        List<AudioTrackContext> audioTrackInfoList = playlistContext.getTracks();

        int trackInfoRspLineLimit = Math.min(audioTrackInfoList.size(), 7);

        for (int i = firstPlaying ? 1 : 0; i < trackInfoRspLineLimit; i++) {
            AudioTrackContext trackContext = audioTrackInfoList.get(i);
                queuedTracksRsp.append(trackContext.getTrackPresent()).append("\n");
        }

        if(audioTrackInfoList.size() > trackInfoRspLineLimit) {
            queuedTracksRsp.append("And ")
                    .append(audioTrackInfoList.size() - trackInfoRspLineLimit).append(" more");
        }

        Message rsp = new MessageBuilder(new InfoMessage("Queued tracks:", queuedTracksRsp.toString()).template()).build();

        playlistContext.getRequestingChannel().sendMessage(rsp).queue();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void skipMultiple(int countOfSkips) {
        if (countOfSkips >= queue.size()) {
            this.queue.clear();
            this.player.stopTrack();
        } else {
            for (int i = 1; i < countOfSkips; i++) {
                this.queue.remove();
            }
            this.nextTrack();
        }
    }

    @Override
    public void nextTrack() {
        if (repeatingTrack != null) {
            this.player.startTrack(repeatingTrack.makeClone(), false);
        } else if (queue.isEmpty()) {
            player.stopTrack();
        } else {
            AudioTrackContext context = this.queue.poll();
            this.player.startTrack(context.getTrack(), false);
            context.getRequestingChannel().sendMessage(getPlayingTrackMessage(context.getTrackPresent()))
                    .queue(x -> ServiceAggregator.getInstance().get(SelfFloodController.class).addMessage(MessageType.PLAYER_NOW_PLAYING, x.getTextChannel().getIdLong(), x.getIdLong()));
        }
    }

    @Override
    public void clearQueue() {
        this.repeatingTrack = null;
        this.queue.clear();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void repeatTrack() {
        this.repeatingTrack = player.getPlayingTrack();
    }

    @Override
    public void stopRepeating() {
        if (this.repeatingTrack != null) {
            this.repeatingTrack = null;
        }
    }

    @Override
    public void mixQueue() {
        Set<AudioTrackContext> tempQueue = new HashSet<>(this.queue);
        this.queue.clear();
        queue.addAll(tempQueue);
    }

    private Message getPlayingTrackMessage(String trackTitle) {
        return new MessageBuilder(new InfoMessage("Now playing:", trackTitle).template())
                .setActionRows(createPlayerButtons(false))
                .build();
    }

    private ActionRow createPlayerButtons(boolean isOnPause) {
        return ActionRow.of(Arrays.asList(
                PLAYER_BUTTON.STOP.button(false),
                isOnPause ? PLAYER_BUTTON.RESUME.button(false) : PLAYER_BUTTON.PAUSE.button(false),
                PLAYER_BUTTON.SKIP.button(false)
        ));
    }

    @Override
    public List<String> getQueuedTracks() {
        return this.queue.stream().map(AudioTrackContext::getTrackPresent).collect(Collectors.toList());
    }
}