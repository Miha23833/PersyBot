package com.persybot.adapters;

import com.persybot.Bot;
import com.persybot.command.CommandContext;
import com.persybot.command.impl.CommandContextImpl;
import com.persybot.command.service.impl.TextCommandServiceImpl;
import com.persybot.enums.TEXT_COMMAND;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.utils.EnumUtils;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
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
        if (event.getMessage().getContentRaw().startsWith(Bot.DEFAULT_PREFIX) && !event.getMessage().getContentRaw().equals(Bot.DEFAULT_PREFIX)) {
            CommandContext context = new CommandContextImpl(event, Bot.DEFAULT_PREFIX);

            try {
                if (EnumUtils.isInEnumIgnoreCase(TEXT_COMMAND.class, context.getCommand()))
                aggregator.getCommand(context.getCommand()).execute(context);
            } catch (IllegalArgumentException e) {
                PersyBotLogger.BOT_LOGGER.error(e);
            }
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getComponentId().equals("1")) {
            event.reply("ЛЁША ПОШЁЛ НАХУЙ").queue(); // send a message in the channel
        } else if (event.getComponentId().equals("emoji")) {
            event.editMessage("That button didn't say click me").queue(); // update the message
        }
    }
}
