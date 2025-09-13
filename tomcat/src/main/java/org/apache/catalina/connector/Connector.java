package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;

    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.executor = Executors.newFixedThreadPool(maxThreads);
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void run() {
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
        while (!stopped) {
            connect();
        }
    }

    private void connect() {
        try {
            process(serverSocket.accept());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection);
        executor.execute(processor);
        logExecutorStats(executor);
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
            executor.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int checkPort(final int port) {
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(final int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }

    private void logExecutorStats(ExecutorService executor) {
        if (executor instanceof ThreadPoolExecutor tpe) {
            var queue = tpe.getQueue();
            log.info("""
            
            ----------Pool stats---------- 
              poolSize={}
              corePoolSize={}
              maxPoolSize={}
              largestPoolSize={} 
              activeCount={}
              completedTaskCount={}
              taskCount={} 
              queueSize={}
              queueRemainingCapacity={}
            """,
                    tpe.getPoolSize(),           // poolSize: 현재 풀 내 총 스레드 수(유휴+활성)
                    tpe.getCorePoolSize(),       // corePoolSize: 코어 스레드 수
                    tpe.getMaximumPoolSize(),    // maxPoolSize: 최대 스레드 수
                    tpe.getLargestPoolSize(),    // largestPoolSize: 역사상 최대로 늘어난 스레드 수
                    tpe.getActiveCount(),        // activeCount: 현재 실행 중인 스레드 수
                    tpe.getCompletedTaskCount(), // completedTaskCount: 완료된 전체 작업 수
                    tpe.getTaskCount(),          // taskCount: 제출된 전체 작업 수(완료+대기+실행 포함)
                    queue.size(),                // queueSize: 큐에 대기 중인 작업 수
                    queue.remainingCapacity()    // queueRemainingCapacity큐의 남은 수용량
            );
        } else {
            log.warn("Executor is not a ThreadPoolExecutor, cannot inspect internals.");
        }
    }
}
