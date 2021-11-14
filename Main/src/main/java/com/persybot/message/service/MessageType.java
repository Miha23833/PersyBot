package com.persybot.message.service;

// TODO load count from config
public enum MessageType {
    PLAYER_NOW_PLAYING(2),
    PLAYER_PAUSE_RESUME(2),
    PLAYER_INFO(200),


    ERROR(5);


    public int getMaxMessagesCount() {
        return maxMessagesCount;
    }

    private final int maxMessagesCount;

    MessageType() {
        this.maxMessagesCount = 50;
    }

    MessageType(int maxMessagesCount) {
        this.maxMessagesCount = maxMessagesCount;
    }
}
