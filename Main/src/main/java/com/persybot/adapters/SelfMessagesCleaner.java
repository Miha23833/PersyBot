package com.persybot.adapters;

import com.persybot.enums.BUTTON_ID;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class SelfMessagesCleaner extends ListenerAdapter {
    private final Map<Long, Map<Long, Queue<Message>>> guild_textChannel_Messages;
    private final Map<Long, Map<Long, Queue<Message>>> guild_textChannel_messagesWithButtons;
    private final Set<String> playerButtonIds;

    private final int messageLimitInHistory;

    public SelfMessagesCleaner(int messageLimitInHistory) {
        playerButtonIds = Arrays.stream(BUTTON_ID.values()).map(BUTTON_ID::getId).collect(Collectors.toSet());
        this.messageLimitInHistory = messageLimitInHistory;
        guild_textChannel_Messages = new ConcurrentHashMap<>();
        guild_textChannel_messagesWithButtons = new ConcurrentHashMap<>();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getJDA().getSelfUser().getIdLong() != event.getAuthor().getIdLong()) {
            return;
        }

        long guildId = event.getGuild().getIdLong();
        long textChannelId = event.getChannel().getIdLong();

        if (event.getMessage().getActionRows().size() > 0) {
            if (containsExactlyPlayerButtons(event.getMessage())) {
                removeSelfPreviousMessagesPlayerButtons(event);
                addToMessages(guild_textChannel_messagesWithButtons, guildId, textChannelId, event.getMessage());
            }
        }
        else {
            addToMessages(guild_textChannel_Messages, guildId, textChannelId, event.getMessage());
        }
    }

    private void removeSelfPreviousMessagesWithoutButtons(GuildMessageReceivedEvent event) {
        long guildId = event.getGuild().getIdLong();
        long textChannelId = event.getChannel().getIdLong();

        if (guild_textChannel_Messages.containsKey(guildId)) {
            if (guild_textChannel_Messages.containsKey(textChannelId)) {
                List<String> messagesToRemove = guild_textChannel_Messages.get(guildId).get(textChannelId)
                        .stream()
                        .map(Message::getId)
                        .collect(Collectors.toList());

                event.getChannel().deleteMessagesByIds(messagesToRemove).queue();

                guild_textChannel_Messages.get(guildId).get(textChannelId).clear();
            }
        }
    }

    private void removeSelfPreviousMessagesPlayerButtons(GuildMessageReceivedEvent event) {
        long guildId = event.getGuild().getIdLong();
        long textChannelId = event.getChannel().getIdLong();

        if (guild_textChannel_messagesWithButtons.containsKey(guildId)) {
            if (guild_textChannel_messagesWithButtons.get(guildId).containsKey(textChannelId)) {
                List<Message> messagesToRemovePlayerButtons = guild_textChannel_messagesWithButtons.get(guildId).get(textChannelId)
                        .stream().filter(this::containsExactlyPlayerButtons)
                        .collect(Collectors.toList());

                guild_textChannel_messagesWithButtons.clear();
                switch (messagesToRemovePlayerButtons.size()) {
                    case 0: return;
                    case 1: removeButtons(messagesToRemovePlayerButtons.get(0));
                    break;
                    default: messagesToRemovePlayerButtons.forEach(this::removeButtons);
                }

            }
        }
    }

    private void removeButtons(Message message) {
        new MessageActionImpl(message.getJDA(), Route.Messages.EDIT_MESSAGE.compile(message.getChannel().getId(), message.getId()), message.getChannel()).setActionRows().queue();
    }

    private boolean containsExactlyPlayerButtons(Message message) {
        return message.getButtons().stream().map(Component::getId).filter(Objects::nonNull).allMatch(playerButtonIds::contains);
    }

    private void addToMessages(Map<Long, Map<Long, Queue<Message>>> pool, long guildId, long textChannelId, Message message) {
        guild_textChannel_messagesWithButtons.computeIfAbsent(guildId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(textChannelId, k -> new ConcurrentLinkedQueue<>()).add(message);
    }

    private void updateMessages(TextChannel channel, List<Message> messages) {
        messages.forEach(msg -> channel.editMessageById(msg.getId(), msg).queue());
    }

    private void updateMessage(TextChannel textChannel, Message message) {
        textChannel.editMessageById(message.getId(), message).queue();
    }
}
