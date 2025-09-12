package org.apache.catalina.connector;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolManager {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);

    private static final int DEFAULT_CORE_THREAD_COUNT = 32;
    private static final int DEFAULT_MAX_THREAD_COUNT = 64;
    private static final long DEFAULT_KEEP_ALIVE_SECONDS = 60;
    private static final int DEFAULT_MAX_WAIT_QUEUE_SIZE = 100;

    private ThreadPoolManager() {
    }

    public static ThreadPoolExecutor createDefaultThreadPoolExecutor() {
        return createThreadPoolExecutor(
                DEFAULT_CORE_THREAD_COUNT,
                DEFAULT_MAX_THREAD_COUNT,
                DEFAULT_KEEP_ALIVE_SECONDS,
                DEFAULT_MAX_WAIT_QUEUE_SIZE
        );
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(
            final int coreThreads,
            final int maxThreads,
            final long keepAliveSeconds,
            final int maxWaitQueueSize
    ) {
        final int checkedCoreThreads = checkCoreThreadCount(coreThreads);
        final int checkedMaxThreads = checkMaxThreadCount(maxThreads, checkedCoreThreads);
        final long checkedKeepAliveSeconds = checkKeepAliveSeconds(keepAliveSeconds);
        final int checkedMaxWaitQueueSize = checkMaxWaitQueueSize(maxWaitQueueSize);

        return new ThreadPoolExecutor(
                checkedCoreThreads,
                checkedMaxThreads,
                checkedKeepAliveSeconds,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(checkedMaxWaitQueueSize)
        );
    }

    private static int checkCoreThreadCount(final int coreThreads) {
        return Math.max(coreThreads, DEFAULT_CORE_THREAD_COUNT);
    }

    private static int checkMaxThreadCount(final int maxThreads, final int coreThreads) {
        if (coreThreads > maxThreads) {
            log.warn(
                    "detected core counts bigger than max count: change max count to bigger value between maxCount={} and coreCount={}",
                    maxThreads, coreThreads);
            return Math.max(coreThreads, DEFAULT_CORE_THREAD_COUNT);
        }
        return Math.max(maxThreads, DEFAULT_MAX_THREAD_COUNT);
    }

    private static long checkKeepAliveSeconds(final long keepAliveSeconds) {
        return Math.max(keepAliveSeconds, DEFAULT_KEEP_ALIVE_SECONDS);
    }

    private static int checkMaxWaitQueueSize(final int maxWaitQueueSize) {
        return Math.max(maxWaitQueueSize, DEFAULT_MAX_WAIT_QUEUE_SIZE);
    }
}
