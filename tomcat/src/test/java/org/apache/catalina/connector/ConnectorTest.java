package org.apache.catalina.connector;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    @Test
    void limitsConcurrentRequestsAndReusesWorkers() throws Exception {
        final var workers = ConcurrentHashMap.<Thread>newKeySet();
        final var started = new CountDownLatch(2);
        final var release = new CountDownLatch(1);
        final Controller controller = (request, response) -> {
            workers.add(Thread.currentThread());
            started.countDown();
            if (!release.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("요청 처리 대기 시간 초과");
            }
            response.body("ok");
        };
        final var mapping = new RequestMapping(Map.of("/", controller), controller);
        final int port = availablePort();
        final var connector = new Connector(port, 100, 2, mapping);
        connector.start();

        try (final var first = new Socket("localhost", port);
             final var second = new Socket("localhost", port);
             final var third = new Socket("localhost", port)) {
            sendRequest(first);
            sendRequest(second);
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

            sendRequest(third);
            release.countDown();

            assertResponse(first);
            assertResponse(second);
            assertResponse(third);
            assertThat(workers).hasSize(2);
        } finally {
            release.countDown();
            connector.stop();
        }

        for (final var worker : workers) {
            worker.join(5000);
            assertThat(worker.isAlive()).isFalse();
        }
    }

    private int availablePort() throws IOException {
        try (final var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private void sendRequest(final Socket socket) throws IOException {
        socket.setSoTimeout(5000);
        socket.getOutputStream().write("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n"
                .getBytes(StandardCharsets.UTF_8));
        socket.getOutputStream().flush();
    }

    private void assertResponse(final Socket socket) throws IOException {
        final var response = new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(response).startsWith("HTTP/1.1 200 OK\r\n").endsWith("\r\n\r\nok");
    }
}
