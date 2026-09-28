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
import java.util.function.Function;

final class Poller implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Poller.class);

    private final Selector selector;
    private final Executor executor;
    private final Function<byte[], byte[]> processor;
    private final ConcurrentLinkedQueue<SocketChannel> pendingConnections = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<NioConnection> pendingResponses = new ConcurrentLinkedQueue<>();
    private volatile boolean stopped;

    Poller(final Selector selector, final Executor executor, final Function<byte[], byte[]> processor) {
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
                registerPendingResponses();
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

    private void registerPendingResponses() {
        NioConnection connection;
        while ((connection = pendingResponses.poll()) != null) {
            final SelectionKey key = connection.channel().keyFor(selector);
            if (key == null || !key.isValid()) {
                close(connection.channel());
                continue;
            }
            key.interestOps(SelectionKey.OP_WRITE);
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
        if (key.isReadable()) {
            read(key);
        }
        if (key.isValid() && key.isWritable()) {
            write(key);
        }
    }

    private void read(final SelectionKey key) throws IOException {
        final NioConnection connection = (NioConnection) key.attachment();
        final int readBytes = connection.read();
        if (readBytes < 0) {
            close(key, connection);
            return;
        }
        if (connection.isRequestComplete()) {
            key.interestOps(0);
            execute(connection);
        }
    }

    private void write(final SelectionKey key) throws IOException {
        final NioConnection connection = (NioConnection) key.attachment();
        connection.write();
        if (connection.isResponseComplete()) {
            close(key, connection);
        }
    }

    private void execute(final NioConnection connection) {
        try {
            executor.execute(() -> process(connection));
        } catch (RejectedExecutionException e) {
            close(connection.channel());
            log.warn("Request rejected because the thread pool and work queue are full.");
        }
    }

    private void process(final NioConnection connection) {
        try {
            final byte[] responseBytes = processor.apply(connection.requestBytes());
            connection.prepareResponse(responseBytes);
            pendingResponses.add(connection);
            selector.wakeup();
        } catch (RuntimeException e) {
            close(connection.channel());
            log.error(e.getMessage(), e);
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
