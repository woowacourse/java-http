package org.apache.catalina.thread;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ThreadContainer {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final List<WorkerThread> workers = new ArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    public ThreadContainer(int maxThreadCount) {
        if (maxThreadCount < 1) {
            throw new IllegalArgumentException("maxThreadCount must be greater than 0");
        }
        startWorkers(maxThreadCount);
    }

    private void startWorkers(int maxThreadCount) {
        for (int index = 0; index < maxThreadCount; index++) {
            WorkerThread worker = new WorkerThread(
                    "http-worker-" + index,
                    taskQueue,
                    running
            );
            workers.add(worker);
            worker.start();
        }
    }

    public void execute(Runnable task) {
        Objects.requireNonNull(task);
        if (!running.get()) {
            throw new RejectedExecutionException("쓰레드 풀은 종료되었습니다.");
        }
        taskQueue.offer(task);
    }

    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            workers.forEach(Thread::interrupt);
            taskQueue.clear();
        }
    }
}
