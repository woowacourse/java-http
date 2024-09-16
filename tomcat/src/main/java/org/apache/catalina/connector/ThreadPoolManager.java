package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    private final ThreadPoolExecutor threadPoolExecutor;

    public ThreadPoolManager(int maxThreads, int acceptCount) {
        this.threadPoolExecutor = new ThreadPoolExecutor(maxThreads, maxThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(acceptCount));
    }

    public void run(Http11Processor processor) {
        threadPoolExecutor.execute(processor);
    }

    public void terminate() {
        threadPoolExecutor.shutdown();
    }

    public int getPoolSize() {
        return threadPoolExecutor.getPoolSize();
    }

    public int getQueueSize() {
        return threadPoolExecutor.getQueue().size();
    }
}
