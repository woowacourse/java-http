package org.apache.coyote;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedThreadPool {

    private static final Logger log = LoggerFactory.getLogger(FixedThreadPool.class);

    private final ExecutorService executorService;

    public FixedThreadPool(int poolSize) {
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public void run(Runnable runnable) {
        try {
            executorService.submit(loggedTask(runnable));
        } catch (RejectedExecutionException | NullPointerException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Runnable loggedTask(Runnable runnable) {
        return () -> {
            String threadName = Thread.currentThread().getName();
            log.info("Task assigned on thread: " + threadName);
            runnable.run();
            log.info("Task completed on thread: " + threadName);
        };
    }
}
