package com.persybot.audio;

import com.persybot.service.Service;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public interface PlayerManagerService extends Service {
    void loadAndPlay(TextChannel channel, String trackUrl);

    void skip(Guild guild);

    void setVolume(Guild guild, int volume);
}
