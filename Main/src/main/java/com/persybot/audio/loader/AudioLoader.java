package com.persybot.audio.loader;

import net.dv8tion.jda.api.entities.TextChannel;

public interface AudioLoader {
    void load(String url, TextChannel requestingChannel);
}
