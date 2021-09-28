package com.persybot.channel;

import com.persybot.audio.AudioPlayer;
import com.persybot.db.model.impl.DiscordServerSettings;

public interface Channel {
    AudioPlayer getAudioPlayer();

    DiscordServerSettings getServerSettings();

    long getLastPlayerMessageId();

    void setLastPlayerMessageId(Long id);
}
