package com.persybot.audio;

import com.persybot.message.PLAYER_BUTTON;
import com.persybot.message.template.impl.InfoMessage;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.Arrays;

public interface PlayerStateSender {
    default Message getPlayingTrackMessage(String trackTitle, boolean isPaused) {
        return new MessageBuilder(new InfoMessage("Now playing:", trackTitle).template())
                .setActionRows(createPlayerButtons(isPaused))
                .build();
    }

    default ActionRow createPlayerButtons(boolean isOnPause) {
        return ActionRow.of(Arrays.asList(
                PLAYER_BUTTON.STOP.button(false),
                isOnPause ? PLAYER_BUTTON.RESUME.button(false) : PLAYER_BUTTON.PAUSE.button(false),
                PLAYER_BUTTON.SKIP.button(false)
        ));
    }
}
