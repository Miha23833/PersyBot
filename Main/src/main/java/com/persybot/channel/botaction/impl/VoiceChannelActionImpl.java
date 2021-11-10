package com.persybot.channel.botaction.impl;

import com.persybot.channel.Channel;
import com.persybot.channel.botaction.VoiceChannelAction;
import com.persybot.logger.impl.PersyBotLogger;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class VoiceChannelActionImpl extends AbstractBotAction implements VoiceChannelAction {

    public VoiceChannelActionImpl(Channel actingChannel) {
        super(actingChannel);
    }

    @Override
    public void joinChannel(VoiceChannel channelToConnect) {
        try {
            this.actingChannel.getGuild().getAudioManager().openAudioConnection(channelToConnect);
            this.staticData.getGuildsWithActiveVoiceChannel().put(channelToConnect.getGuild().getIdLong(), System.currentTimeMillis());
        } catch (Exception e) {
            PersyBotLogger.BOT_LOGGER.error(e);
        }
    }

    @Override
    public void leaveChannel() {
        try {
            AudioManager manager = actingChannel.getGuild().getAudioManager();

            if (manager.getConnectedChannel() != null) {
                long connectedChannelId = manager.getConnectedChannel().getGuild().getIdLong();

                manager.closeAudioConnection();

                this.staticData.getGuildsWithActiveVoiceChannel().remove(connectedChannelId);
            }
        } catch (Exception e) {
            PersyBotLogger.BOT_LOGGER.error(e);
        }
    }
}
