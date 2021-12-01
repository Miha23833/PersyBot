package com.persybot.adapters;

import com.persybot.channel.impl.ChannelImpl;
import com.persybot.channel.service.ChannelService;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.service.ServiceAggregator;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.staticdata.StaticData;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class ServiceUpdaterAdapter extends ListenerAdapter {
    private final ChannelService channelService;
    private final StaticData staticData;
    private final DBService dbService;

    public ServiceUpdaterAdapter() {
        ServiceAggregator serviceAggregator = ServiceAggregatorImpl.getInstance();
        channelService = serviceAggregator.getService(ChannelService.class);
        dbService = serviceAggregator.getService(DBService.class);
        staticData = serviceAggregator.getService(StaticData.class);
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        if (event.getGuild().getSelfMember().getIdLong() == event.getMember().getIdLong()) {
            staticData.getGuildsWithActiveVoiceChannel().remove(event.getGuild().getIdLong());
        }
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        if (event.getGuild().getSelfMember().getIdLong() == event.getMember().getIdLong()) {
            staticData.getGuildsWithActiveVoiceChannel().put(event.getGuild().getIdLong(), System.currentTimeMillis());
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        long serverId = event.getGuild().getIdLong();
        DiscordServerSettings serverSettings;
        try {
            serverSettings = dbService.getOrInsertIfNotExists(DiscordServerSettings.class, serverId, new DiscordServerSettings(serverId));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
            serverSettings = new DiscordServerSettings(serverId);
        }

        channelService.addChannel(serverId, new ChannelImpl(channelService.getAudioPlayerManager(), serverSettings, event.getGuild()));
    }
}
