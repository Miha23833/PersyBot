package com.persybot.channel.botaction.impl;

import com.persybot.channel.Channel;
import com.persybot.channel.botaction.PlayerAction;
import com.persybot.db.service.DBService;
import com.persybot.service.impl.ServiceAggregatorImpl;

import java.net.URI;
import java.net.URISyntaxException;

public class PlayerActionImpl extends AbstractBotAction implements PlayerAction {
    public PlayerActionImpl(Channel actingChannel) {
        super(actingChannel);
    }

    @Override
    public void playSong(String songLink) {
        if (!isUrl(songLink)) {
            songLink = "ytsearch:" + songLink;
        }
        actingChannel.getAudioPlayer().loadAndPlay(songLink);
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

    private static boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }


}
