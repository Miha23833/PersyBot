package com.persybot.perfomance.voiceactivity.impl;

import com.persybot.audio.GuildAudioPlayer;
import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.service.impl.ServiceAggregator;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoiceInactivityChecker {
    private final ScheduledExecutorService executorService;

    private final ChannelService channelService;

    private final long maxInactivityTime;
    private final long checkPauseMillis;
    private final Map<Long, Long> guildsLastActivity;

    public VoiceInactivityChecker(Map<Long, Long> guildsLastActivity, long checkPauseMillis, long maxInactivityTime) {
        this.checkPauseMillis = checkPauseMillis;
        this.guildsLastActivity = guildsLastActivity;
        this.maxInactivityTime = maxInactivityTime;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.channelService = ServiceAggregator.getInstance().get(ChannelService.class);
    }

    public void run() {
        this.executorService.scheduleWithFixedDelay(
                () -> guildsLastActivity.keySet().forEach(this::checkInactivity), 0, checkPauseMillis, TimeUnit.MILLISECONDS);
    }

    private void checkInactivity(long channelId) {
        Channel channel = channelService.getChannel(channelId);

        if (channel != null) {
            long currentTime = System.currentTimeMillis();
            VoiceChannel connectedChannel = channel.getGuild().getAudioManager().getConnectedChannel();

            if (connectedChannel == null) {
                return;
            }
            else if (!channel.hasInitiatedAudioPlayer()) {
                channel.voiceChannelAction().leaveChannel();
                return;
            }

            GuildAudioPlayer audioPlayer = channel.getAudioPlayer();

            if (connectedChannel.getMembers().size() < 2 || !audioPlayer.isPlaying()) {
                if (currentTime - guildsLastActivity.get(channelId) > maxInactivityTime) {
                    channel.playerAction().stopMusic();
                    channel.voiceChannelAction().leaveChannel();
                }
            }
            else {
                guildsLastActivity.put(channelId, currentTime);
            }
        }
    }


}
