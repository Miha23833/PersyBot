package com.persybot.channel.botaction;

public interface PlayerAction {
    void playSong(String songLink);
    void skipSong();
    void stopMusic();
    void setVolume(Integer volume);
}
