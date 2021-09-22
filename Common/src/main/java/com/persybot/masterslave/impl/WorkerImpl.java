package com.persybot.masterslave.impl;

import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.masterslave.Worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkerImpl implements Worker {
    private final BlockingQueue<Runnable> tasks;

    private final AtomicBoolean onPause = new AtomicBoolean(true);
    private final AtomicBoolean isBusy = new AtomicBoolean(false);

    public WorkerImpl(BlockingQueue<Runnable> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void start() {
        onPause.set(false);
        while (!onPause.get()) {
            doWork();
        }
    }

    @Override
    public void stop() {
        onPause.set(true);
    }

    @Override
    public boolean isBusy() {
        return isBusy.get();
    }

    private void doWork() {
        if (!tasks.isEmpty()) {
            Runnable task = tasks.remove();
            try {
                isBusy.set(true);
                task.run();
            } catch (Exception e) {
                PersyBotLogger.BOT_LOGGER.error(e);
            } finally {
                isBusy.set(false);
            }
        }
    }
}
