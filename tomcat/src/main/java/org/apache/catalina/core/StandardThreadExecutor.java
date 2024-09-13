package org.apache.catalina.core;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StandardThreadExecutor implements Executor {
    private static final int DEFAULT_MAX_THREADS = 250;

    private final ExecutorService executorService;

    public StandardThreadExecutor() {
        this(DEFAULT_MAX_THREADS);
    }

    public StandardThreadExecutor(int maxThreads) {
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
