package org.apache.catalina.connector;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkerThreadPool extends ThreadPoolExecutor {

    private static final int DEFAULT_CORE_POOL_SIZE = 2;
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 2;

    public WorkerThreadPool() {
        this(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE);
    }

    public WorkerThreadPool(
            final int corePoolSize,
            final int maximumPoolSize
    ) {
        super(
                corePoolSize,
                maximumPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
        prestartCoreThread();
    }
}
