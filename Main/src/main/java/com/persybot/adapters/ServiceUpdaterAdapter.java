package com.persybot.adapters;

import com.persybot.channel.service.ChannelImpl;
import com.persybot.channel.service.ChannelService;
import com.persybot.db.model.impl.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.service.ServiceAggregator;
import com.persybot.service.impl.ServiceAggregatorImpl;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class ServiceUpdaterAdapter extends ListenerAdapter {
    private final ChannelService channelService;
    private final DBService dbService;

    public ServiceUpdaterAdapter() {
        ServiceAggregator serviceAggregator = ServiceAggregatorImpl.getInstance();
        channelService = serviceAggregator.getService(ChannelService.class);
        dbService = serviceAggregator.getService(DBService.class);
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

        channelService.addChannel(serverId, new ChannelImpl(channelService.getAudioPlayerManager(), serverSettings));
    }
}
