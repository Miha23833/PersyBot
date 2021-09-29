package com.persybot.enums;

public enum BUTTON_ID {
    PLAYER_PAUSE("player-pause"),
    PLAYER_RESUME("player-resume"),
    PLAYER_SKIP("player-skip"),
    PLAYER_STOP("player-stop"),
    PLAYER_QUEUE("player-queue");

    private final String id;
    BUTTON_ID(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
