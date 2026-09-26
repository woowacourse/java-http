package org.apache.catalina.connector;

import org.apache.coyote.Adapter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    @Test
    void limitsActiveAndQueuedRequestsAndRejectsExcessConnection() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Adapter adapter = (request, response) -> {
            started.countDown();
            release.await();
            response.setBody("ok");
        };

        try (TestServer server = startServer(adapter, limits(5_000, 3_000, 500));
             Socket first = request(server.port());
             Socket second = request(server.port())) {
            assertThat(started.await(2, TimeUnit.SECONDS)).isTrue();
            awaitQueuedRequests(server.connector(), 1);

            try (Socket excess = request(server.port())) {
                assertConnectionClosed(excess);
            }

            release.countDown();
            assertThat(response(first)).startsWith("HTTP/1.1 200 OK\r\n");
            assertThat(response(second)).startsWith("HTTP/1.1 200 OK\r\n");
        } finally {
            release.countDown();
        }
    }

    @Test
    void expiresQueuedConnectionBeforeItIsProcessed() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger();
        Adapter adapter = (request, response) -> {
            calls.incrementAndGet();
            started.countDown();
            release.await();
            response.setBody("ok");
        };

        try (TestServer server = startServer(adapter, limits(5_000, 150, 500));
             Socket first = request(server.port());
             Socket queued = request(server.port())) {
            assertThat(started.await(2, TimeUnit.SECONDS)).isTrue();
            awaitQueuedRequests(server.connector(), 1);

            assertConnectionClosed(queued);
            assertThat(calls.get()).isEqualTo(1);
            release.countDown();
            assertThat(response(first)).startsWith("HTTP/1.1 200 OK\r\n");
        } finally {
            release.countDown();
        }
    }

    @Test
    void timesOutIncompleteRequest() throws Exception {
        try (TestServer server = startServer((request, response) -> response.setBody("ok"),
                limits(150, 1_000, 500));
             Socket client = new Socket("127.0.0.1", server.port())) {
            client.setSoTimeout(2_000);
            client.getOutputStream().write("GET / HTTP/1.1\r\nHost: localhost\r\n"
                    .getBytes(StandardCharsets.US_ASCII));

            assertThat(response(client)).startsWith("HTTP/1.1 408 Request Timeout\r\n");
        }
    }

    @Test
    void stopClosesBlockedConnectionsAfterGracePeriod() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Adapter adapter = (request, response) -> {
            started.countDown();
            new CountDownLatch(1).await();
        };

        try (TestServer server = startServer(adapter, limits(5_000, 3_000, 100));
             Socket active = request(server.port());
             Socket queued = request(server.port())) {
            assertThat(started.await(2, TimeUnit.SECONDS)).isTrue();
            awaitQueuedRequests(server.connector(), 1);

            long startedAt = System.nanoTime();
            server.connector().stop();
            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);

            assertThat(elapsedMillis).isLessThan(2_000);
            assertConnectionClosed(queued);
        }
    }

    @Test
    void stopClosesConnectionsBlockedOnSocketReadAndTerminatesWorkers() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        try (TestServer server = startServer((request, response) -> calls.incrementAndGet(),
                limits(5_000, 5_000, 100));
             Socket active = new Socket("127.0.0.1", server.port())) {
            active.setSoTimeout(2_000);
            active.getOutputStream().write("GET / HTTP/1.1\r\nHost: localhost\r\n"
                    .getBytes(StandardCharsets.US_ASCII));
            try (Socket queued = request(server.port())) {
                // 첫 작업은 헤더의 끝을 기다리므로 다음 요청은 큐에서 대기한다.
                awaitQueuedRequests(server.connector(), 1);
                long startedAt = System.nanoTime();

                server.connector().stop();

                assertThat(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt))
                        .isLessThan(2_000);
                assertConnectionClosed(active);
                assertConnectionClosed(queued);
                assertThat(server.connector().workersTerminated()).isTrue();
                assertThat(calls.get()).isZero();
            }
        }
    }

    private Connector.Limits limits(int readTimeoutMillis, int queueWaitTimeoutMillis,
                                    int shutdownTimeoutMillis) {
        return new Connector.Limits(1, 1, readTimeoutMillis, queueWaitTimeoutMillis,
                shutdownTimeoutMillis);
    }

    private TestServer startServer(Adapter adapter, Connector.Limits limits) throws IOException {
        int port;
        try (ServerSocket reservation = new ServerSocket(0)) {
            port = reservation.getLocalPort();
        }
        Connector connector = new Connector(port, 100, adapter, limits);
        connector.start();
        return new TestServer(connector, port);
    }

    private Socket request(int port) throws IOException {
        Socket socket = new Socket("127.0.0.1", port);
        socket.setSoTimeout(2_000);
        socket.getOutputStream().write("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n"
                .getBytes(StandardCharsets.US_ASCII));
        return socket;
    }

    private String response(Socket socket) throws IOException {
        return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void assertConnectionClosed(Socket socket) throws IOException {
        try {
            assertThat(socket.getInputStream().read()).isEqualTo(-1);
        } catch (SocketException e) {
            assertThat(e.getMessage()).isNotBlank();
        }
    }

    private void awaitQueuedRequests(Connector connector, int count) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (connector.queuedRequestCount() != count && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        assertThat(connector.queuedRequestCount()).isEqualTo(count);
    }

    private record TestServer(Connector connector, int port) implements AutoCloseable {

        @Override
        public void close() {
            connector.stop();
        }
    }
}
