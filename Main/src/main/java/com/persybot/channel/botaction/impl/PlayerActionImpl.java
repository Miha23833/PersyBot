package com.persybot.channel.botaction.impl;

import com.persybot.channel.Channel;
import com.persybot.channel.botaction.PlayerAction;
import com.persybot.db.service.DBService;
import com.persybot.service.impl.ServiceAggregatorImpl;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URI;

public class PlayerActionImpl extends AbstractBotAction implements PlayerAction {
    public PlayerActionImpl(Channel actingChannel) {
        super(actingChannel);
    }

    @Override
    public void playSong(String songLink, TextChannel requestingChannel) {
        if (!isUrl(songLink)) {
            songLink = "ytsearch:" + songLink;
        }

        actingChannel.getAudioPlayer().loadAndPlay(songLink, requestingChannel);
    }

    @Override
    public void skipSong() {
        actingChannel.getAudioPlayer().skip();
    }

    @Override
    public void stopMusic() {
        actingChannel.getAudioPlayer().stop();
    }

    @Override
    public void setVolume(Integer volume) {
        actingChannel.getAudioPlayer().setVolume(volume);

        actingChannel.getServerSettings().setVolume(volume);
        ServiceAggregatorImpl.getInstance().getService(DBService.class).update(actingChannel.getServerSettings());
    }

    @Override
    public void pauseSong() {
        actingChannel.getAudioPlayer().pause();
    }

    @Override
    public void resumePlayer() {
        actingChannel.getAudioPlayer().resume();
    }

    private static boolean isUrl(String url) {
        try {
            new URI(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
