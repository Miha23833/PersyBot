package com.persybot.audio.audioloadreslt;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public interface AudioPlaylistContext {
    List<AudioTrackContext> getTracks();

    TextChannel getRequestingChannel();
}
