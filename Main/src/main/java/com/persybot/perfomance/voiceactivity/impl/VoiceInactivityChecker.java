package com.persybot.perfomance.voiceactivity.impl;

import com.persybot.audio.AudioPlayer;
import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.event.EventListener;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.service.impl.ServiceAggregatorImpl;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoiceInactivityChecker extends Thread {
    private final long maxInactivityTime;
    private final long checkPause;
    private final Map<Long, Long> guildsLastActivity;

    private final List<EventListener> eventListeners = new ArrayList<>();

    public VoiceInactivityChecker(Map<Long, Long> guildsLastActivity, long checkPause, long maxInactivityTime) {
        this.checkPause = checkPause;
        this.guildsLastActivity = guildsLastActivity;
        this.maxInactivityTime = maxInactivityTime;
    }

    public void addEventListeners(EventListener eventListener) {
        this.eventListeners.add(eventListener);
    }

    @Override
    public void run() {
        while (true) {
            for (Long channelId: guildsLastActivity.keySet()) {
                checkInactivity(channelId);
            }
            try {
                Thread.sleep(checkPause);
            } catch (InterruptedException e) {
                PersyBotLogger.BOT_LOGGER.error(e);
            }
        }
    }

    private void checkInactivity(long channelId) {
        Channel channel = ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(channelId);

        if (channel != null) {
            long currentTime = System.currentTimeMillis();
            AudioPlayer audioPlayer = channel.getAudioPlayer();
            VoiceChannel connectedChannel = channel.getGuild().getAudioManager().getConnectedChannel();

            int connectedMembersCount = 0;
            if (connectedChannel != null) {
                connectedMembersCount = connectedChannel.getMembers().size();
            }

            if (connectedMembersCount < 2 || !audioPlayer.isPlaying() || audioPlayer.onPause()) {
                if (currentTime - guildsLastActivity.get(channelId) > maxInactivityTime) {
                    channel.playerAction().stopMusic();
                    // TODO: add message "Leave channel due to inactivity" when add "Last acting text channel"
                    channel.voiceChannelAction().leaveChannel();
                }
            }
            else {
                guildsLastActivity.put(channelId, currentTime);
            }
        }
    }



}
