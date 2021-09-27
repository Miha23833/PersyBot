package com.persybot.masterslave;

import java.util.concurrent.RunnableFuture;

public interface TaskMaster<T> {
    void addTask(RunnableFuture<T> task);

    void start();

    void stop();
}
