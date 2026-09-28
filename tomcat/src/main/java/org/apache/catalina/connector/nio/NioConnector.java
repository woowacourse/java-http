package org.apache.catalina.connector.nio;

import org.apache.coyote.http11.Http11Processor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class NioConnector {

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int DEFAULT_MAX_QUEUE_SIZE = 100;
    private static final int TERMINATION_TIMEOUT_SECONDS = 5;

    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final ExecutorService executorService;
    private final Acceptor acceptor;
    private final Poller poller;
    private final Thread acceptorThread;
    private final Thread pollerThread;

    public NioConnector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, DEFAULT_MAX_QUEUE_SIZE);
    }

    public NioConnector(final int port, final int acceptCount,
                        final int maxThreads, final int maxQueueSize) {
        validate(port, acceptCount, maxThreads, maxQueueSize);
        try {
            this.serverChannel = ServerSocketChannel.open();
            this.serverChannel.bind(new InetSocketAddress(port), acceptCount);
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.executorService = createExecutorService(maxThreads, maxQueueSize);
        this.poller = new Poller(selector, executorService, Http11Processor::process);
        this.acceptor = new Acceptor(serverChannel, poller::register);
        this.acceptorThread = new Thread(acceptor, "nio-acceptor");
        this.pollerThread = new Thread(poller, "nio-poller");
        this.acceptorThread.setDaemon(true);
        this.pollerThread.setDaemon(true);
    }

    public void start() {
        pollerThread.start();
        acceptorThread.start();
    }

    public void stop() {
        acceptor.stop();
        closeServerChannel();
        shutdownExecutorService();
        poller.stop();
        join(acceptorThread);
        join(pollerThread);
        closeSelector();
    }

    public int getLocalPort() {
        try {
            return ((InetSocketAddress) serverChannel.getLocalAddress()).getPort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ExecutorService createExecutorService(final int maxThreads, final int maxQueueSize) {
        return new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(maxQueueSize),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void closeServerChannel() {
        try {
            serverChannel.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void closeSelector() {
        try {
            selector.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void join(final Thread thread) {
        try {
            thread.join(TimeUnit.SECONDS.toMillis(TERMINATION_TIMEOUT_SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void validate(final int port, final int acceptCount,
                          final int maxThreads, final int maxQueueSize) {
        if (port < 0 || 65535 < port) {
            throw new IllegalArgumentException("port는 0 이상 65535 이하여야 합니다.");
        }
        if (acceptCount <= 0 || maxThreads <= 0 || maxQueueSize <= 0) {
            throw new IllegalArgumentException("acceptCount, maxThreads, maxQueueSize는 0보다 커야 합니다.");
        }
    }
}
