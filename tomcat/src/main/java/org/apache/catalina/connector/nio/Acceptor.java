package org.apache.catalina.connector.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

final class Acceptor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Acceptor.class);

    private final ServerSocketChannel serverChannel;
    private final Consumer<SocketChannel> registrar;
    private volatile boolean stopped;

    Acceptor(final ServerSocketChannel serverChannel, final Consumer<SocketChannel> registrar) {
        this.serverChannel = serverChannel;
        this.registrar = registrar;
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                acceptConnection();
            } catch (IOException e) {
                if (!stopped) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    void acceptConnection() throws IOException {
        final SocketChannel channel = serverChannel.accept();
        if (channel == null) {
            return;
        }

        channel.configureBlocking(false);
        registrar.accept(channel);
    }

    void stop() {
        stopped = true;
    }
}
