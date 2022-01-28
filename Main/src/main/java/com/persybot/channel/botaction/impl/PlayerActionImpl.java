package com.persybot.channel.botaction.impl;

import com.persybot.channel.Channel;
import com.persybot.channel.botaction.PlayerAction;
import com.persybot.db.service.DBService;
import com.persybot.service.impl.ServiceAggregator;
import net.dv8tion.jda.api.entities.TextChannel;

import static com.persybot.utils.URLUtil.isUrl;

public class PlayerActionImpl extends AbstractBotAction implements PlayerAction {
    public PlayerActionImpl(Channel actingChannel) {
        super(actingChannel);
    }

    @Override
    public void playSong(String songLink, TextChannel requestingChannel) {
        if (!isUrl(songLink)) {
            songLink = "ytsearch:" + songLink;
        }
        resumePlayer();

        actingChannel.getAudioPlayer().scheduleTrack(songLink, requestingChannel);
    }

    @Override
    public void skipSong() {
        actingChannel.getAudioPlayer().skip();
    }

    @Override
    public void skipSong(int countOfSkips) {
        actingChannel.getAudioPlayer().skip(countOfSkips);
    }

    @Override
    public void stopMusic() {
        if (this.actingChannel.hasInitiatedAudioPlayer()) {
            actingChannel.getAudioPlayer().stop();
        }
    }

    @Override
    public void setVolume(Integer volume) {
        actingChannel.getAudioPlayer().setVolume(volume);

        actingChannel.getServerSettings().setVolume(volume);
        ServiceAggregator.getInstance().get(DBService.class).updateDiscordServerSettings(actingChannel.getServerSettings());
    }

    @Override
    public void pauseSong() {
        actingChannel.getAudioPlayer().pause();
    }

    @Override
    public void resumePlayer() {
        actingChannel.getAudioPlayer().resume();
    }

    @Override
    public void repeat() {
        actingChannel.getAudioPlayer().repeat();
    }

    @Override
    public void mixQueue() {
        actingChannel.getAudioPlayer().mixQueue();
    }


}
