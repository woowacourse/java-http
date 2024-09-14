package org.apache.catalina.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class TomcatThreadPool extends ThreadPoolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TomcatThreadPool.class);
    private static final RejectedExecutionHandler saturationPolicy = new ThreadPoolExecutor.AbortPolicy();

    public TomcatThreadPool(
            int maxThreads,
            int acceptCount
    ) {
        super(
                maxThreads,
                maxThreads,
                0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(acceptCount),
                saturationPolicy
        );
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        logger.debug("tomcat thread pool accept connection, now active count = {}", getActiveCount());
        super.beforeExecute(t, r);
    }
}
