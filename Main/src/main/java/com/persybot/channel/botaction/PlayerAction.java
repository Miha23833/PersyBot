package com.persybot.channel.botaction;

import net.dv8tion.jda.api.entities.TextChannel;

public interface PlayerAction {
    void playSong(String songLink, TextChannel requestingChannel);
    void skipSong();
    void skipSong(int countOfSkips);
    void stopMusic();
    void setVolume(Integer volume);
    void pauseSong();
    void resumePlayer();
    void repeat();
    void mixQueue();
}
