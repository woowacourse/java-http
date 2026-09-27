package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Test
    @DisplayName("동시 연결 요청을 하나의 워커 스레드가 순차 처리한다")
    void reusesSingleWorkerForConcurrentConnections() throws Exception {
        int port = findAvailablePort();
        int requestCount = 3;

        CountDownLatch requestStarted = new CountDownLatch(1);
        CountDownLatch requestsSent = new CountDownLatch(requestCount);
        CountDownLatch releaseRequests = new CountDownLatch(1);

        AtomicInteger activeRequests = new AtomicInteger();
        AtomicInteger maxActiveRequests = new AtomicInteger();

        Queue<Thread> workerThreads = new ConcurrentLinkedQueue<>();

        Controller controller = request -> {
            int active = activeRequests.incrementAndGet();
            maxActiveRequests.accumulateAndGet(active, Math::max);
            workerThreads.add(Thread.currentThread());
            requestStarted.countDown();

            try {
                if (!releaseRequests.await(5, TimeUnit.SECONDS)) {
                    throw new IOException("Timed out waiting to release request processing");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException(e);
            } finally {
                activeRequests.decrementAndGet();
            }

            return HttpResponse.empty(200, "OK");
        };

        RequestMapping requestMapping = new RequestMapping(
                Map.of("/pool", controller),
                request -> HttpResponse.empty(404, "Not Found")
        );

        Connector connector = new Connector(port, 100, 1, requestMapping);
        ExecutorService clients = Executors.newFixedThreadPool(requestCount);

        connector.start();

        try {
            ArrayList<Future<String>> responses = new ArrayList<>();
            for (int i = 0; i < requestCount; i++) {
                responses.add(clients.submit(() -> sendRequest(port, requestsSent)));
            }

            assertThat(requestStarted.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(requestsSent.await(5, TimeUnit.SECONDS)).isTrue();
            releaseRequests.countDown();

            for (Future<String> response : responses) {
                assertThat(response.get(5, TimeUnit.SECONDS)).startsWith("HTTP/1.1 200 OK");
            }

            assertThat(maxActiveRequests.get()).isEqualTo(1);
            assertThat(workerThreads).hasSize(requestCount);
            assertThat(workerThreads).containsOnly(workerThreads.peek());
        } finally {
            releaseRequests.countDown();
            connector.stop();
            clients.shutdownNow();
        }
    }

    private String sendRequest(int port, CountDownLatch requestsSent) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", port)) {
            socket.setSoTimeout(5000);
            socket.getOutputStream().write(
                    "GET /pool HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes(StandardCharsets.UTF_8)
            );
            socket.getOutputStream().flush();
            requestsSent.countDown();
            return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
