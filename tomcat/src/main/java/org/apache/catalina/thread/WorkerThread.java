package org.apache.catalina.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WorkerThread extends Thread {

    private final BlockingQueue<Runnable> taskQueue;
    private final AtomicBoolean running;

    public WorkerThread(
            String name,
            BlockingQueue<Runnable> taskQueue,
            AtomicBoolean running
    ) {
        super(name);
        this.taskQueue = taskQueue;
        this.running = running;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Runnable task = taskQueue.take();
                runTask(task);
            } catch (InterruptedException e) {
                if (!running.get()) {
                    return;
                }
            }
        }
    }

    private void runTask(Runnable task) {
        try {
            task.run();
        } catch (RuntimeException e) {
            getUncaughtExceptionHandler().uncaughtException(this, e);
        }
    }
}
