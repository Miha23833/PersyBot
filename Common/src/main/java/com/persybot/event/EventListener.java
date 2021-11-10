package com.persybot.event;

public interface EventListener {
    void onEvent(Event event);

    default void emitEvent(Event event) {
        this.onEvent(event);
    };
}
