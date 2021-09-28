package com.persybot.channel.service;

import com.persybot.channel.Channel;
import com.persybot.service.Service;

public interface ChannelService extends Service {
    Channel getChannel(Long id);
}
