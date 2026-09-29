package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class ConnectorTest {

    @Test
    void rejectsInvalidMaxThreads() throws IOException {
        for (final int maxThreads : new int[]{0, -1}) {
            final int port = freePort();

            assertThatThrownBy(() -> new Connector(port, 100, maxThreads))
                    .isInstanceOf(IllegalArgumentException.class);

            try (final var socket = new ServerSocket(port)) {
                assertThat(socket.getLocalPort()).isEqualTo(port);
            }
        }
    }

    @Test
    void rejectsExcessConnection() throws IOException, InterruptedException {
        // given
        final var processing = new CountDownLatch(1);
        final var release = new CountDownLatch(1);
        final var connector = new Connector(freePort(), 100, 1);
        final var busySocket = new StubSocket("") {
            @Override
            public InputStream getInputStream() {
                processing.countDown();
                try {
                    release.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return super.getInputStream();
            }
        };

        try {
            connector.process(busySocket);
            assertThat(processing.await(5, TimeUnit.SECONDS)).isTrue();
            for (int i = 0; i < 100; i++) {
                connector.process(new StubSocket(""));
            }

            // when
            final var rejectedSocket = new StubSocket("");
            connector.process(rejectedSocket);

            // then
            assertThat(rejectedSocket.isClosed()).isTrue();
        } finally {
            release.countDown();
            connector.stop();
        }
    }

    private int freePort() throws IOException {
        try (final var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
