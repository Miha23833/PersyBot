package com.persybot.adapters;

import com.persybot.channel.impl.ChannelImpl;
import com.persybot.channel.service.ChannelService;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.service.ServiceAggregator;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.staticdata.StaticData;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Properties;

public class ServiceUpdaterAdapter extends ListenerAdapter {
    private final ChannelService channelService;
    private final StaticData staticData;
    private final DBService dbService;

    private final String defaultPrefix;

    public ServiceUpdaterAdapter(Properties botConfig) {
        ServiceAggregator serviceAggregator = ServiceAggregatorImpl.getInstance();
        channelService = serviceAggregator.getService(ChannelService.class);
        dbService = serviceAggregator.getService(DBService.class);
        staticData = serviceAggregator.getService(StaticData.class);

        this.defaultPrefix = botConfig.getProperty("bot.prefix.default");
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

        Optional<DiscordServer> discordServer = this.dbService.getDiscordServer(serverId);
        Optional<DiscordServerSettings> serverSettings = this.dbService.getDiscordServerSettings(serverId);

        if (discordServer.isPresent()) {
            if (serverSettings.isEmpty()) {
                this.dbService.saveDiscordServerSettings(getDefaultDiscordServerSettings(serverId));

                serverSettings = this.dbService.getDiscordServerSettings(serverId);
            }
        } else {
            DiscordServer newDiscordServerRecord = getDefaultDiscordServer(serverId);
            dbService.saveDiscordServer(newDiscordServerRecord);
            DiscordServerSettings newServerSettings = getDefaultDiscordServerSettings(serverId);
            dbService.saveDiscordServerSettings(newServerSettings);

            serverSettings = this.dbService.getDiscordServerSettings(serverId);
        }

        if (serverSettings.isEmpty()) {
            throw new RuntimeException("Cannot save/get discord server settings with id = " + serverId);
        }

        channelService.addChannel(serverId, new ChannelImpl(channelService.getAudioPlayerManager(), serverSettings.get(), event.getGuild()));
    }

    private DiscordServer getDefaultDiscordServer(Long serverId) {
        return new DiscordServer(serverId, 1);
    }

    private DiscordServerSettings getDefaultDiscordServerSettings(long serverId) {
        return new DiscordServerSettings(serverId, 100, defaultPrefix);
    }

    private boolean isDiscordServerExists(long serverId) {
        return this.dbService.getDiscordServer(serverId).isPresent();
    }
}
