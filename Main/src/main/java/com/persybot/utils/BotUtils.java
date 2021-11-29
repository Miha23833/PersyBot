package com.persybot.utils;

import com.persybot.logger.impl.PersyBotLogger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
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
            PersyBotLogger.BOT_LOGGER.error("Cannot get member voice state.");
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

    static void sendPersonalMessage(@NotNull String text, @NotNull User user) {
        user.openPrivateChannel().queue((channel) -> channel.sendMessage(text).queue());
    }
}
