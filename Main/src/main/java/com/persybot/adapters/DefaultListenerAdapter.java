package com.persybot.adapters;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.CommandContext;
import com.persybot.command.impl.CommandContextImpl;
import com.persybot.command.service.impl.TextCommandServiceImpl;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.service.impl.ServiceAggregatorImpl;
import com.persybot.utils.EnumUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DefaultListenerAdapter extends ListenerAdapter {
    private final TextCommandServiceImpl aggregator;
    public DefaultListenerAdapter(TextCommandServiceImpl aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String prefix = ServiceAggregatorImpl.getInstance().getService(ChannelService.class).getChannel(event.getGuild().getIdLong()).getServerSettings().getPrefix();
        if (event.getMessage().getContentRaw().startsWith(prefix) && !event.getMessage().getContentRaw().equals(prefix)) {
            CommandContext context = new CommandContextImpl(event, prefix);
            try {
                if (EnumUtils.isInEnumIgnoreCase(TEXT_COMMAND.class, context.getCommand())) {
                    aggregator.getCommand(context.getCommand()).execute(context);
                }
            } catch (IllegalArgumentException e) {
                PersyBotLogger.BOT_LOGGER.error(e);
            }
        }
    }
}
