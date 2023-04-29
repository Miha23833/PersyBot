package com.persybot.audio;

import com.persybot.message.PLAYER_BUTTON;
import com.persybot.message.template.impl.InfoMessage;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public interface PlayerStateSender {
    default MessageCreateData getPlayingTrackMessage(String trackTitle, boolean isPaused) {
        return MessageCreateBuilder.from(new InfoMessage("Now playing:", trackTitle).template())
                .setActionRow(
                        PLAYER_BUTTON.STOP.button(false),
                        isPaused ? PLAYER_BUTTON.RESUME.button(false) : PLAYER_BUTTON.PAUSE.button(false),
                        PLAYER_BUTTON.SKIP.button(false))
                .build();
    }
}
