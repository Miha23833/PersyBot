package com.persybot.audio.audioloadreslt;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

public interface AudioTrackContext {
    AudioTrack getTrack();

    TextChannel getRequestingChannel();
}
