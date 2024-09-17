package org.apache.catalina.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;

public class ThreadExecutor implements Executor {

    private static final int DEFAULT_MAX_THREADS = 50;

    private final ExecutorService executor;

    public ThreadExecutor() {
        this.executor = Executors.newFixedThreadPool(DEFAULT_MAX_THREADS);
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        executor.execute(command);
    }
}
