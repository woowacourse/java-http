package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.coyote.http11.controller.RequestMapping;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Test
    void process_reuses_worker_thread() throws IOException {
        // given
        final Set<Thread> workers = ConcurrentHashMap.newKeySet();
        final RequestMapping requestMapping = new RequestMapping();
        requestMapping.addController("/worker", (request, response) -> {
            workers.add(Thread.currentThread());
            response.setBody("worker response");
        });

        final Connector connector = new Connector(0, 100, 1, requestMapping);
        connector.start();

        try {
            // when
            for (int i = 0; i < 3; i++) {
                try (final Socket socket = sendRequest(connector, "/worker")) {
                    assertThat(readResponse(socket))
                            .contains("HTTP/1.1 200 OK")
                            .endsWith("worker response");
                }
            }

            // then
            assertThat(workers).hasSize(1);
        } finally {
            connector.stop();
        }
    }

    @Test
    void process_queues_request_when_all_workers_are_busy() throws Exception {
        // given
        final CountDownLatch workersStarted = new CountDownLatch(2);
        final CountDownLatch releaseWorkers = new CountDownLatch(1);
        final Set<Thread> workers = ConcurrentHashMap.newKeySet();
        final RequestMapping requestMapping = new RequestMapping();
        requestMapping.addController("/busy", (request, response) -> {
            workers.add(Thread.currentThread());
            workersStarted.countDown();
            if (!releaseWorkers.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("작업 스레드 대기 시간이 초과되었습니다.");
            }
            response.setBody("busy response");
        });
        requestMapping.addController("/queued", (request, response) -> {
            workers.add(Thread.currentThread());
            response.setBody("queued response");
        });

        final Connector connector = new Connector(0, 100, 2, requestMapping);
        connector.start();

        try (
                final Socket first = sendRequest(connector, "/busy");
                final Socket second = sendRequest(connector, "/busy")
        ) {
            assertThat(workersStarted.await(5, TimeUnit.SECONDS)).isTrue();

            // when
            try (final Socket queued = sendRequest(connector, "/queued")) {
                queued.setSoTimeout(200);

                // then
                assertThatThrownBy(() -> queued.getInputStream().read())
                        .isInstanceOf(SocketTimeoutException.class);

                releaseWorkers.countDown();
                queued.setSoTimeout(5000);

                assertThat(readResponse(first)).endsWith("busy response");
                assertThat(readResponse(second)).endsWith("busy response");
                assertThat(readResponse(queued)).endsWith("queued response");
                assertThat(workers).hasSize(2);
            }
        } finally {
            releaseWorkers.countDown();
            connector.stop();
        }
    }

    @Test
    void stop_finishes_running_request_and_terminates_worker() throws Exception {
        // given
        final CountDownLatch workerStarted = new CountDownLatch(1);
        final CountDownLatch releaseWorker = new CountDownLatch(1);
        final AtomicReference<Thread> worker = new AtomicReference<>();
        final RequestMapping requestMapping = new RequestMapping();
        requestMapping.addController("/busy", (request, response) -> {
            worker.set(Thread.currentThread());
            workerStarted.countDown();
            if (!releaseWorker.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("작업 스레드 대기 시간이 초과되었습니다.");
            }
            response.setBody("completed response");
        });

        final Connector connector = new Connector(0, 100, 1, requestMapping);
        connector.start();

        try (final Socket socket = sendRequest(connector, "/busy")) {
            assertThat(workerStarted.await(5, TimeUnit.SECONDS)).isTrue();

            // when
            connector.stop();
            releaseWorker.countDown();

            // then
            assertThat(readResponse(socket)).endsWith("completed response");
            worker.get().join(5000);
            assertThat(worker.get().isAlive()).isFalse();
            assertThatThrownBy(() -> {
                try (final Socket rejected = sendRequest(connector, "/busy")) {
                    readResponse(rejected);
                }
            }).isInstanceOf(IOException.class);
        } finally {
            releaseWorker.countDown();
            connector.stop();
        }
    }

    @Test
    void constructor_rejects_non_positive_max_threads() {
        // when & then
        assertThatThrownBy(() -> new Connector(0, 100, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Connector(0, 100, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Socket sendRequest(
            final Connector connector,
            final String path
    ) throws IOException {
        final Socket socket = new Socket(
                InetAddress.getLoopbackAddress(),
                connector.getLocalPort()
        );
        socket.setSoTimeout(5000);

        final String request = "GET " + path
                + " HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        socket.getOutputStream().write(request.getBytes(StandardCharsets.UTF_8));
        socket.getOutputStream().flush();
        return socket;
    }

    private String readResponse(final Socket socket) throws IOException {
        return new String(
                socket.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
    }
}
