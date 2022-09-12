package org.apache.catalina.executor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Executor implements java.util.concurrent.Executor {

    private static final int DEFAULT_MIN_SPARE_THREADS = 10;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int DEFAULT_MAX_QUEUE_SIZE = 1000;
    private static final long DEFAULT_KEEP_ALIVE_TIME = 10L;

    private static final Logger log = LoggerFactory.getLogger(Executor.class);

    private final ThreadPoolExecutor threadPool;

    public Executor() {
        this(DEFAULT_MIN_SPARE_THREADS, DEFAULT_MAX_THREADS, DEFAULT_MAX_QUEUE_SIZE);
    }

    public Executor(final int minSpareThreads,
                    final int maxThreads,
                    final int maxQueueSize) {
        this.threadPool = new ThreadPoolExecutor(minSpareThreads, maxThreads,
                DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<>(maxQueueSize));
    }

    @Override
    public void execute(Runnable command) {
        threadPool.execute(command);
        log.info("pool size: {}", threadPool.getPoolSize());
    }
}
