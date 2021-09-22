package com.persybot.masterslave;

import java.util.function.Consumer;

public interface TaskMaster {
    void addTask(Runnable task);

    void setMaxWorkersAndFill(int maxSlaves);
}
