package com.persybot.channel.service;

import com.persybot.channel.Channel;
import com.persybot.service.Service;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public interface ChannelService extends Service {
    Channel getChannel(Long id);

    void addChannel(Long id, Channel channel);

    AudioPlayerManager getAudioPlayerManager();
}
