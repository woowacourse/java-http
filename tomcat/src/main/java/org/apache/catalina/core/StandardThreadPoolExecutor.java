package org.apache.catalina.core;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StandardThreadPoolExecutor extends ThreadPoolExecutor {

    private static final int DEFAULT_MAX_THREADS = 250;

    public StandardThreadPoolExecutor(int acceptCount) {
        super(DEFAULT_MAX_THREADS, DEFAULT_MAX_THREADS,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(acceptCount));
    }

    public StandardThreadPoolExecutor(int maxThreads, int acceptCount) {
        super(maxThreads, maxThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(acceptCount));
    }
}
