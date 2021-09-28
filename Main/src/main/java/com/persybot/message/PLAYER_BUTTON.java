package com.persybot.message;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

public enum PLAYER_BUTTON {
    PAUSE(ButtonStyle.SUCCESS, "pause", Emoji.fromUnicode("U+25B6")),
    RESUME(ButtonStyle.SUCCESS, "resume", Emoji.fromUnicode("U+25B6")),
    SKIP(ButtonStyle.PRIMARY, "skip", Emoji.fromUnicode("U+23ED")),
    STOP(ButtonStyle.DANGER, "stop", Emoji.fromUnicode("U+23F9"));

    private final ButtonStyle style;
    private final String id;
    private final Emoji playerImg;


    PLAYER_BUTTON(ButtonStyle style, String id, Emoji playerImg) {
        this.style = style;
        this.id = id;
        this.playerImg = playerImg;
    }

    public Button button(boolean isDisabled) {
        return Button.of(style, id, playerImg).withDisabled(isDisabled);
    }
}
