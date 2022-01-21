package com.persybot.adapters;

import com.persybot.channel.service.ChannelService;
import com.persybot.command.ButtonCommandContext;
import com.persybot.command.TextCommandContext;
import com.persybot.command.button.impl.ButtonCommandContextImpl;
import com.persybot.command.impl.TextCommandContextImpl;
import com.persybot.command.service.ButtonCommandService;
import com.persybot.command.service.TextCommandService;
import com.persybot.enums.BUTTON_ID;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.service.impl.ServiceAggregator;
import com.persybot.utils.EnumUtils;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DefaultListenerAdapter extends ListenerAdapter {
    private final TextCommandService textCommandPool;
    private final ButtonCommandService buttonCommandPool;
    private final ChannelService channelService;

    public DefaultListenerAdapter(TextCommandService textCommandPool, ButtonCommandService buttonCommandPool) {
        this.textCommandPool = textCommandPool;
        this.buttonCommandPool = buttonCommandPool;
        this.channelService = ServiceAggregator.getInstance().get(ChannelService.class);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String prefix = channelService.getChannel(event.getGuild().getIdLong()).getServerSettings().getPrefix();
        if (event.getMessage().getContentRaw().startsWith(prefix) && !event.getMessage().getContentRaw().equals(prefix)) {
            TextCommandContext context = new TextCommandContextImpl(event, prefix);
            try {
                if (textCommandPool.containsCommand(context.getCommand())) {
                    textCommandPool.getCommand(context.getCommand()).execute(context);
                }
            } catch (IllegalArgumentException e) {
                PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getButton() == null) {
            return;
        }
        ButtonCommandContext context = new ButtonCommandContextImpl(event);
        try {
            if (EnumUtils.isInEnumIgnoreCase(BUTTON_ID.class, context.getButtonId())) {
                buttonCommandPool.getCommand(context.getButtonId()).execute(context);
                if (!event.getInteraction().isAcknowledged()) {
                    event.getInteraction().deferEdit().queue();
                }
            }
        }  catch (IllegalArgumentException e) {
            PersyBotLogger.BOT_LOGGER.error(e.getMessage(), e);
        }
    }
}
