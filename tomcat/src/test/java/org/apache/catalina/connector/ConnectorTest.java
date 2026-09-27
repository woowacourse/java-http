package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    private Connector connector;

    @AfterEach
    void tearDown() {
        if (connector != null) {
            connector.stop();
        }
    }

    @Test
    void reusesWorkerThread() throws Exception {
        // given
        final int port = findAvailablePort();
        final Set<Thread> workerThreads = ConcurrentHashMap.newKeySet();
        final Controller controller = (request, response) -> {
            workerThreads.add(Thread.currentThread());
            response.ok("Hello world!", "text/plain");
        };
        connector = new Connector(port, 100, 1, 1, new RequestMapping(Map.of("/", controller)));
        connector.start();

        // when
        final String firstResponse = sendRequest(port);
        final String secondResponse = sendRequest(port);

        // then
        assertThat(firstResponse).startsWith("HTTP/1.1 200 OK").endsWith("Hello world!");
        assertThat(secondResponse).startsWith("HTTP/1.1 200 OK").endsWith("Hello world!");
        assertThat(workerThreads).hasSize(1);
    }

    private int findAvailablePort() throws IOException {
        try (var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private String sendRequest(final int port) throws IOException {
        try (var socket = new Socket("localhost", port)) {
            socket.setSoTimeout(5000);
            final String request = "GET / HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
            socket.getOutputStream().write(request.getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().flush();
            return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
