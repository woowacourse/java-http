package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.catalina.controller.RequestMapping;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class ConnectorTest {

    @Test
    void reusesAWorkerForQueuedRequests() throws Exception {
        final var firstStarted = new CountDownLatch(1);
        final var releaseFirst = new CountDownLatch(1);
        final var secondStarted = new CountDownLatch(1);
        final var firstWorker = new AtomicReference<Thread>();
        final var secondWorker = new AtomicReference<Thread>();
        final var mapping = new RequestMapping(Map.of(
                "/first", (request, response) -> {
                    firstWorker.set(Thread.currentThread());
                    firstStarted.countDown();
                    if (!releaseFirst.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("first request timed out");
                    }
                    response.body("first".getBytes(StandardCharsets.UTF_8));
                },
                "/second", (request, response) -> {
                    secondWorker.set(Thread.currentThread());
                    secondStarted.countDown();
                    response.body("second".getBytes(StandardCharsets.UTF_8));
                }
        ), (request, response) -> {});
        final var connector = new Connector(0, 100, 1, 1, mapping);

        try {
            connector.process(request("/first"));
            assertThat(firstStarted.await(5, TimeUnit.SECONDS)).isTrue();

            connector.process(request("/second"));
            assertThat(secondStarted.getCount()).isEqualTo(1);

            releaseFirst.countDown();
            assertThat(secondStarted.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(secondWorker.get()).isSameAs(firstWorker.get());
        } finally {
            releaseFirst.countDown();
            connector.stop();
        }
    }

    @Test
    void closesAConnectionRejectedByTheFullPoolAndQueue() throws Exception {
        final var firstStarted = new CountDownLatch(1);
        final var releaseFirst = new CountDownLatch(1);
        final var secondStarted = new CountDownLatch(1);
        final var mapping = new RequestMapping(Map.of(
                "/first", (request, response) -> {
                    firstStarted.countDown();
                    if (!releaseFirst.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("first request timed out");
                    }
                },
                "/second", (request, response) -> secondStarted.countDown()
        ), (request, response) -> {});
        final var connector = new Connector(0, 100, 1, 1, mapping);

        try {
            connector.process(request("/first"));
            assertThat(firstStarted.await(5, TimeUnit.SECONDS)).isTrue();

            connector.process(request("/second"));
            final var rejected = request("/third");
            connector.process(rejected);

            assertThat(rejected.isClosed()).isTrue();
            assertThat(rejected.output()).isEmpty();

            releaseFirst.countDown();
            assertThat(secondStarted.await(5, TimeUnit.SECONDS)).isTrue();
        } finally {
            releaseFirst.countDown();
            connector.stop();
        }
    }

    @Test
    void closesRunningAndQueuedConnectionsWhenStopping() throws Exception {
        final var mapping = new RequestMapping(Map.of(), (request, response) -> {});
        final var connector = new Connector(0, 100, 1, 1, mapping);
        final var running = new BlockingSocket();
        final var queued = request("/queued");

        try {
            connector.process(running);
            assertThat(running.awaitReading()).isTrue();
            connector.process(queued);

            connector.stop();

            assertThat(running.isClosed()).isTrue();
            assertThat(queued.isClosed()).isTrue();
        } finally {
            running.close();
            queued.close();
            connector.stop();
        }
    }

    @Test
    void stopReleasesARealSocketWaitingForMoreRequestBytes() throws Exception {
        final var mapping = new RequestMapping(Map.of(), (request, response) -> {});
        final var connector = new Connector(0, 100, 1, 1, mapping);

        try (var listener = new ServerSocket(0);
             var client = new Socket("127.0.0.1", listener.getLocalPort());
             var accepted = listener.accept()) {
            client.setSoTimeout(3_000);
            client.getOutputStream().write("GET / HTTP/1.1\r\nHost: ".getBytes(StandardCharsets.UTF_8));
            client.getOutputStream().flush();
            connector.process(accepted);

            connector.stop();

            assertThat(accepted.isClosed()).isTrue();
            assertThat(client.getInputStream().read()).isEqualTo(-1);
        } finally {
            connector.stop();
        }
    }

    private StubSocket request(String path) {
        return new StubSocket("GET " + path + " HTTP/1.1\r\nHost: localhost\r\n\r\n");
    }

    private static final class BlockingSocket extends Socket {

        private final CountDownLatch reading = new CountDownLatch(1);
        private final CountDownLatch closed = new CountDownLatch(1);

        @Override
        public InputStream getInputStream() {
            return new InputStream() {
                @Override
                public int read() throws IOException {
                    reading.countDown();
                    try {
                        if (!closed.await(5, TimeUnit.SECONDS)) {
                            throw new IOException("socket was not closed");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException(e);
                    }
                    throw new SocketException("socket closed");
                }
            };
        }

        @Override
        public OutputStream getOutputStream() {
            return OutputStream.nullOutputStream();
        }

        boolean awaitReading() throws InterruptedException {
            return reading.await(5, TimeUnit.SECONDS);
        }

        @Override
        public void close() throws IOException {
            super.close();
            closed.countDown();
        }
    }
}
