package com.persybot.message.service;

public enum MessageType {
    PLAYER_NOW_PLAYING(3),
    PLAYER_INFO(200),
    ERROR(30);


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
