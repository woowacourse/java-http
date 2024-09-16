package org.apache.catalina.connector;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolConnector extends Connector {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREAD_COUNT = 250;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 20000;
    private static final int DEFAULT_MAX_CONNECTION_COUNT = 250;

    private final ThreadPoolExecutor pool;
    private final Function<Socket, Http11Processor> container;

    public ThreadPoolConnector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREAD_COUNT);
    }

    public ThreadPoolConnector(final int port, final int acceptCount, final int maxThreads) {
        super(port, acceptCount);
        this.pool = createThreadPool(maxThreads);
        this.container = (connection) -> new Http11Processor(connection, new CatalinaAdapter());
        log.info("Thread Pool Created! Accept Count: {}, Max Threads: {}", acceptCount, maxThreads);
    }

    ThreadPoolConnector(final int port, final int acceptCount, final int maxThreads,
                        Function<Socket, Http11Processor> container) {
        super(port, acceptCount);
        this.pool = createThreadPool(maxThreads);
        this.container = container;
    }

    private ThreadPoolExecutor createThreadPool(final int maxThreads) {
        return new ThreadPoolExecutor(maxThreads, maxThreads,
                                      DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<>(DEFAULT_MAX_CONNECTION_COUNT));
    }

    @Override
    final void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = container.apply(connection);
        pool.submit(processor);
    }

    int getActiveCount() {
        return pool.getActiveCount();
    }

    int getWaitCount() {
        return pool.getQueue().size();
    }
}
