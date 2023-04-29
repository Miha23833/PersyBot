package com.persybot.adapters.perfomance;

import com.persybot.channel.Channel;
import com.persybot.channel.service.ChannelService;
import com.persybot.config.pojo.BotConfig;
import com.persybot.service.impl.ServiceAggregator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoiceInactivityChecker extends ListenerAdapter {
    private final ScheduledExecutorService executorService;

    private final ChannelService channelService;

    private final long maxInactivityTime;
    private final long checkPauseMillis;

    private final Map<Long, Long> guildsLastActivity;

    public VoiceInactivityChecker(BotConfig config) {
        this.guildsLastActivity = new ConcurrentHashMap<>();

        this.checkPauseMillis = config.activityCheckPauseMillis;
        this.maxInactivityTime = config.maxInactivityTimeMillis;

        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.channelService = ServiceAggregator.getInstance().get(ChannelService.class);
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() != null) {
            if (event.getGuild().getSelfMember().getIdLong() == event.getMember().getIdLong()) {
                guildsLastActivity.remove(event.getGuild().getIdLong());
            }
        } else if (event.getChannelJoined() != null) {
            if (event.getGuild().getSelfMember().getIdLong() == event.getMember().getIdLong()) {
                guildsLastActivity.put(event.getGuild().getIdLong(), System.currentTimeMillis());
            }

        }
    }

    @Override
    public void onStatusChange(@NotNull StatusChangeEvent event) {
        if (event.getNewStatus().equals(JDA.Status.INITIALIZED) ) {
            run();
        }
    }

    private void run() {
        this.executorService.scheduleWithFixedDelay(
                () -> guildsLastActivity.keySet().forEach(this::checkInactivity), 0, checkPauseMillis, TimeUnit.MILLISECONDS);
    }

    private void checkInactivity(long channelId) {
        Channel channel = channelService.getChannel(channelId);

        if (channel != null) {
            long currentTime = System.currentTimeMillis();
            AudioChannelUnion connectedChannel = channel.getGuild().getAudioManager().getConnectedChannel();

            if (connectedChannel == null) {
                return;
            }

            boolean isPlaying = channel.hasInitiatedAudioPlayer() && channel.getAudioPlayer().isPlaying();

            if (connectedChannel.getMembers().size() < 2 || !isPlaying) {
                if (currentTime - guildsLastActivity.get(channelId) > maxInactivityTime) {
                    if (isPlaying) {
                        channel.playerAction().stopMusic();
                    }
                    channel.voiceChannelAction().leaveChannel();
                }
            }
            else {
                guildsLastActivity.put(channelId, currentTime);
            }
        }
    }


}
