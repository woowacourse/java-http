package org.apache.catalina.executor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Executor implements java.util.concurrent.Executor {

    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_MAX_THREADS = 1000;
    private static final int DEFAULT_MAX_QUEUE_SIZE = 20;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 60000;

    private static final Logger log = LoggerFactory.getLogger(Executor.class);

    private final ThreadPoolExecutor threadPool;

    public Executor() {
        this(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_THREADS, DEFAULT_MAX_QUEUE_SIZE, DEFAULT_KEEP_ALIVE_TIME);
    }

    public Executor(final int corePoolSize,
                    final int maxThreads,
                    final int maxQueueSize,
                    final int keepAliveTime) {
        final var workQueue = new LinkedBlockingQueue<Runnable>(maxQueueSize);
        this.threadPool = new ThreadPoolExecutor(corePoolSize, maxThreads, keepAliveTime, TimeUnit.MILLISECONDS,
                workQueue);
    }

    @Override
    public void execute(Runnable command) {
        threadPool.execute(command);
        log.info("pool size: {}", threadPool.getPoolSize());
    }
}
