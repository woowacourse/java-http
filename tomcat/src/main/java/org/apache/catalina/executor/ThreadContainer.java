package org.apache.catalina.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

public class ThreadContainer {

    private static final int DEFAULT_TIMEOUT = 60;
    private final ExecutorService executorService;

    public ThreadContainer(int queueSize, int maxThreads) {
        final int coreCount = Runtime.getRuntime().availableProcessors();
        this.executorService = new ThreadPoolExecutor(
                coreCount * 2,
                maxThreads,
                DEFAULT_TIMEOUT,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize),
                new AbortPolicy()
        );
    }

    public void execute(Runnable command) {
        executorService.execute(command);
    }
}
