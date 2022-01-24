package com.persybot.utils;

import com.persybot.cache.service.CacheService;
import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.message.PAGEABLE_MESSAGE_TYPE;
import com.persybot.message.cache.PageableMessageCache;
import com.persybot.message.template.impl.PagingMessage;
import com.persybot.paginator.PageableMessage;
import com.persybot.service.impl.ServiceAggregator;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface BotUtils {
    static boolean isMemberInVoiceChannel(Member member) {
        return Objects.requireNonNull(member.getVoiceState()).inVoiceChannel();
    }

    static boolean isMemberInSameVoiceChannelAsBot(Member member, Member selfMember) {
        GuildVoiceState memberVoiceState = member.getVoiceState();
        GuildVoiceState selfVoiceState = selfMember.getVoiceState();

        if (memberVoiceState == null || selfVoiceState == null) {
            PersyBotLogger.BOT_LOGGER.error("Cannot get member voice state");
            return false;
        }
        return memberVoiceState.getChannel() != null && memberVoiceState.getChannel().equals(selfVoiceState.getChannel());
    }

    static boolean canSpeak(Member selfMember) {
        return selfMember.hasPermission(Permission.VOICE_SPEAK);
    }

    static boolean canJoin(Member selfMember, VoiceChannel targetChannel) {
        return (targetChannel.getUserLimit() == 0 || targetChannel.getUserLimit() - targetChannel.getMembers().size() > 0)
                && selfMember.hasPermission(Permission.VOICE_CONNECT);
    }

    static boolean isMemberInVoiceChannel(Member selfMember, VoiceChannel targetChannel) {
        return selfMember.getVoiceState() != null && selfMember.getVoiceState().getChannel() != null
                && selfMember.getVoiceState().getChannel().getIdLong() == targetChannel.getIdLong();
    }

    static boolean canWrite(Member selfMember, TextChannel targetChannel) {
        return selfMember.hasPermission(Permission.MESSAGE_WRITE) && targetChannel.canTalk();
    }

    static void sendMessage(@NotNull String text, @NotNull TextChannel channel) {
        channel.sendMessage(text).queue();
    }

    static void sendMessage(@NotNull Message message, @NotNull TextChannel channel) {
        channel.sendMessage(message).queue();
    }

    static void sendPersonalMessage(@NotNull String text, @NotNull User user) {
        user.openPrivateChannel().queue((channel) -> channel.sendMessage(text).queue());
    }

    static void sendPageableMessage(PageableMessage.Builder message, TextChannel channel, PAGEABLE_MESSAGE_TYPE type) {
        sendPageableMessage(
                message,
                channel,
                type,
                ServiceAggregator.getInstance().get(CacheService.class).get(PageableMessageCache.class));
    }

    static void sendPageableMessage(PageableMessage.Builder message, TextChannel channel, PAGEABLE_MESSAGE_TYPE type, PageableMessageCache cache) {
        if (message.size() == 1) {
            sendMessage(new PagingMessage(message.get(0), false, false).template(), channel);
        } else {
            channel.sendMessage(new PagingMessage(message.get(0), false, true).template())
                    .queue(success -> cache.add(success.getTextChannel().getIdLong(), PAGEABLE_MESSAGE_TYPE.PLAYLISTS, message.build(success.getIdLong())));
        }
    }

    static String toHypertext(String text, String link) {
        return "[" + text + "]" + "(" + link + ")";
    }

    static String bold(@NotNull String text) {
        return String.join("", "**", text, "**");
    }
}
