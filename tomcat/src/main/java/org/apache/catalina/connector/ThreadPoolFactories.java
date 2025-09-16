package org.apache.catalina.connector;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolFactories {

    public static ExecutorService ioBoundFixed(
            final int threads,
            final int queueCapacity
    ) {
        return new ThreadPoolExecutor(
                threads, threads,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                namedFactory("FIXED_IO"),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public static ExecutorService cpuBoundElastic(
            final int core,
            final int max,
            final long keepAlive,
            final TimeUnit unit,
            final int queueCapacity
    ) {
        final ThreadPoolExecutor ex = new ThreadPoolExecutor(
                core, max,
                keepAlive, unit,
                new ArrayBlockingQueue<>(queueCapacity),
                namedFactory("ELASTIC_CPU"),
                new ThreadPoolExecutor.AbortPolicy()
        );
        ex.allowCoreThreadTimeOut(true);
        return ex;
    }

    private static ThreadFactory namedFactory(final String poolName) {
        final AtomicInteger seq = new AtomicInteger(1);
        return r -> {
            final Thread t = new Thread(r, poolName + "-" + seq.getAndIncrement());
            t.setDaemon(false);
            return t;
        };
    }

    public static void gracefulShutdown(final ExecutorService es, final Duration timeout) {
        es.shutdown();
        try {
            if (es.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                return;
            }
            es.shutdownNow();
        } catch (final InterruptedException ie) {
            es.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
