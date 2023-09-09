package org.apache.catalina.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TomcatThreadPool extends ThreadPoolExecutor {

    private static final int DEFAULT_CORE_POOL_SIZE = 25;
    private static final int DEFAULT_MAX_POOL_SIZE = 200;
    private static final int DEFAULT_WORK_QUEUE_SIZE = Integer.MAX_VALUE;
    private static final Long DEFAULT_KEEP_ALIVE_TIME = 0L;
    private static final TimeUnit DEFAULT_KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;

    public TomcatThreadPool() {
        super(
            DEFAULT_CORE_POOL_SIZE,
            DEFAULT_MAX_POOL_SIZE,
            DEFAULT_KEEP_ALIVE_TIME,
            DEFAULT_KEEP_ALIVE_TIME_UNIT,
            new LinkedBlockingQueue<>(DEFAULT_WORK_QUEUE_SIZE)
        );
    }

    public TomcatThreadPool(final int maxThreads) {
        super(
            DEFAULT_CORE_POOL_SIZE,
            Math.max(DEFAULT_CORE_POOL_SIZE, maxThreads),
            DEFAULT_KEEP_ALIVE_TIME,
            DEFAULT_KEEP_ALIVE_TIME_UNIT,
            new LinkedBlockingQueue<>(DEFAULT_WORK_QUEUE_SIZE)
        );
    }

    public TomcatThreadPool(final int maxThreads, final int workQueueSize) {
        super(
            DEFAULT_CORE_POOL_SIZE,
            Math.max(DEFAULT_CORE_POOL_SIZE, maxThreads),
            DEFAULT_KEEP_ALIVE_TIME,
            DEFAULT_KEEP_ALIVE_TIME_UNIT,
            new LinkedBlockingQueue<>(Math.max(1, workQueueSize))
        );
    }
}
