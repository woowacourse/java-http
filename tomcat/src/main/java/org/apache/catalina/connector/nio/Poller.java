package org.apache.catalina.connector.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;

final class Poller implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Poller.class);

    private final Selector selector;
    private final Executor executor;
    private final Consumer<NioConnection> processor;
    private final ConcurrentLinkedQueue<SocketChannel> pendingConnections = new ConcurrentLinkedQueue<>();
    private volatile boolean stopped;

    Poller(final Selector selector, final Executor executor, final Consumer<NioConnection> processor) {
        this.selector = selector;
        this.executor = executor;
        this.processor = processor;
    }

    void register(final SocketChannel channel) {
        pendingConnections.add(channel);
        selector.wakeup();
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                registerPendingConnections();
                selector.select();
                processSelectedKeys();
            } catch (IOException e) {
                if (!stopped) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    void registerPendingConnections() {
        SocketChannel channel;
        while ((channel = pendingConnections.poll()) != null) {
            registerWithSelector(channel);
        }
    }

    private void registerWithSelector(final SocketChannel channel) {
        try {
            channel.register(selector, SelectionKey.OP_READ, new NioConnection(channel));
        } catch (IOException e) {
            close(channel);
            log.error(e.getMessage(), e);
        }
    }

    private void processSelectedKeys() throws IOException {
        final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            final SelectionKey key = iterator.next();
            iterator.remove();
            if (key.isValid()) {
                processKey(key);
            }
        }
    }

    void processKey(final SelectionKey key) throws IOException {
        if (!key.isReadable()) {
            return;
        }

        final NioConnection connection = (NioConnection) key.attachment();
        final int readBytes = connection.read();
        if (readBytes < 0) {
            close(key, connection);
            return;
        }
        if (connection.isRequestComplete()) {
            key.cancel();
            execute(connection);
        }
    }

    private void execute(final NioConnection connection) {
        try {
            executor.execute(() -> processor.accept(connection));
        } catch (RejectedExecutionException e) {
            close(connection.channel());
            log.warn("Request rejected because the thread pool and work queue are full.");
        }
    }

    private void close(final SelectionKey key, final NioConnection connection) throws IOException {
        key.cancel();
        connection.close();
    }

    private void close(final SocketChannel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    void stop() {
        stopped = true;
        selector.wakeup();
    }
}
