package com.persybot.masterslave;

public interface Worker {
    void start();

    void stop();

    boolean isBusy();
}
