package com.persybot.audio;

import net.dv8tion.jda.api.entities.TextChannel;

public interface PlayerManager {
    void loadAndPlay(TextChannel channel, String trackUrl);
}
