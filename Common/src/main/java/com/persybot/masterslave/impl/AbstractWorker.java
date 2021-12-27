package com.persybot.masterslave.impl;

import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.masterslave.Worker;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AbstractWorker<T> extends Thread implements Worker {

    protected final BlockingQueue<RunnableFuture<T>> tasks;

    protected final AtomicBoolean isBusy = new AtomicBoolean(false);
    protected final AtomicBoolean isMasterOnPause;

    public AbstractWorker(BlockingQueue<RunnableFuture<T>> tasks, AtomicBoolean isMasterOnPause) {
        this.isMasterOnPause = isMasterOnPause;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        while (!isMasterOnPause.get()) {
            doWork();
        }
    }

    @Override
    public boolean isBusy() {
        return isBusy.get();
    }

    protected void doWork() {
        if (!tasks.isEmpty()) {
            RunnableFuture<T> task = tasks.poll();
            if (task != null) {
                PersyBotLogger.BOT_LOGGER.error(Thread.currentThread().getName() + "Started task");
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
}
