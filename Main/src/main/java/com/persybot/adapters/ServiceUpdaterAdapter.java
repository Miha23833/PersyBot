package com.persybot.adapters;

import com.persybot.channel.impl.ChannelImpl;
import com.persybot.channel.service.ChannelService;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.staticdata.StaticData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Properties;

public class ServiceUpdaterAdapter extends ListenerAdapter {
    private final ChannelService channelService;
    private final StaticData staticData;
    private final DBService dbService;

    private static final Duration JOIN_TIMEOUT_OFFSET = Duration.ofSeconds(30);

    private final String defaultPrefix;

    public ServiceUpdaterAdapter(Properties botConfig) {
        ServiceAggregator serviceAggregator = ServiceAggregator.getInstance();
        channelService = serviceAggregator.get(ChannelService.class);
        dbService = serviceAggregator.get(DBService.class);
        staticData = serviceAggregator.get(StaticData.class);

        this.defaultPrefix = botConfig.getProperty("BOT_PREFIX_DEFAULT");
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
    public void onGuildMemberUpdate(@NotNull GuildMemberUpdateEvent event) {
        OffsetDateTime joinTimeout = OffsetDateTime.now().plus(JOIN_TIMEOUT_OFFSET);
        if (event.getMember().getIdLong() == event.getGuild().getSelfMember().getIdLong()
                && joinTimeout.compareTo(event.getMember().getTimeJoined()) > 0) {
            initializeDiscordServer(event.getGuild());
        }
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        initializeDiscordServer(event.getGuild());
        ServiceAggregator.getInstance().get(DBService.class)
                .getAllEqPresets().orElseThrow( () -> new RuntimeException("Cannot get presets."))
                .forEach(this.staticData::addPreset);
    }

    private DiscordServer getDefaultDiscordServer(Long serverId) {
        return new DiscordServer(serverId, 1);
    }

    private DiscordServerSettings getDefaultDiscordServerSettings(long serverId) {
        return new DiscordServerSettings(serverId, 100, defaultPrefix);
    }

    private void initializeDiscordServer(Guild guild) {
        long serverId = guild.getIdLong();

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

        channelService.addChannel(serverId, new ChannelImpl(channelService.getAudioPlayerManager(), serverSettings.get(), guild));
    }
}
