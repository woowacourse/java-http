package org.apache.catalina.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ExecutorService {

    private final ThreadPoolExecutor executor;

    public ExecutorService(final int maxThreads) {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
    }

    public void submit(final Runnable runnable) {
        this.executor.submit(runnable);
    }
}
