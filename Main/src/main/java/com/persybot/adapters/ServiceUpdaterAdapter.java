package com.persybot.adapters;

import com.persybot.channel.impl.ChannelImpl;
import com.persybot.channel.service.ChannelService;
import com.persybot.config.pojo.BotConfig;
import com.persybot.db.entity.DiscordServer;
import com.persybot.db.entity.DiscordServerSettings;
import com.persybot.db.service.DBService;
import com.persybot.service.impl.ServiceAggregator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

public class ServiceUpdaterAdapter extends ListenerAdapter {
    private final ChannelService channelService;
    private final DBService dbService;

    private static final Duration JOIN_TIMEOUT_OFFSET = Duration.ofSeconds(30);

    private final BotConfig botConfig;

    public ServiceUpdaterAdapter(BotConfig botConfig) {
        ServiceAggregator serviceAggregator = ServiceAggregator.getInstance();
        channelService = serviceAggregator.get(ChannelService.class);
        dbService = serviceAggregator.get(DBService.class);

        this.botConfig = botConfig;
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
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        loadServerToDbIfAbsent(event);
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        loadServerToDbIfAbsent(event);
    }

    private void loadServerToDbIfAbsent(@NotNull GenericGuildEvent event) {
        initializeDiscordServer(event.getGuild());
    }

    private void initializeDiscordServer(Guild guild) {
        long serverId = guild.getIdLong();

        Optional<DiscordServer> discordServerOpt = this.dbService.read(serverId, DiscordServer.class);

        if (discordServerOpt.isEmpty()) {
            ServiceAggregator.getInstance().get(DBService.class).create(getDefaultDiscordServer(serverId));
        }
        channelService.addChannel(serverId, new ChannelImpl(channelService.getAudioPlayerManager(), guild, botConfig));
    }

    private DiscordServer getDefaultDiscordServer(Long serverId) {
        return new DiscordServer(serverId, 0, new DiscordServerSettings((byte) 100, botConfig.defaultPrefix));
    }
}
