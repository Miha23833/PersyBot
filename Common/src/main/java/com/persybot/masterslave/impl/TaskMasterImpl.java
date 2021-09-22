package com.persybot.masterslave.impl;

import com.persybot.logger.impl.PersyBotLogger;
import com.persybot.masterslave.TaskMaster;
import com.persybot.masterslave.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TaskMasterImpl implements TaskMaster {

    final int frequencyOfAskingWorkers;

    BlockingQueue<Runnable> tasks = new LinkedBlockingDeque<>();
    List<Worker> workers = new ArrayList<>();

    public TaskMasterImpl(int frequencyOfAskingWorkers) {
        this.frequencyOfAskingWorkers = frequencyOfAskingWorkers;
    }

    @Override
    public void addTask(Runnable task) {
        tasks.add(task);
    }

    @Override
    public void setMaxWorkersAndFill(int maxSlaves) {
        try {
            workers.forEach(Worker::stop);
            awaitAllWorkersDone();

            workers = new ArrayList<>(workers);
            workers.forEach(worker -> worker = new WorkerImpl(tasks));
        } catch (InterruptedException e) {
            PersyBotLogger.BOT_LOGGER.error(e);
        }
    }

    private void awaitAllWorkersDone() throws InterruptedException {
        while (workers.stream().anyMatch(Worker::isBusy)) {
            Thread.sleep(frequencyOfAskingWorkers);
        }
    }
}
