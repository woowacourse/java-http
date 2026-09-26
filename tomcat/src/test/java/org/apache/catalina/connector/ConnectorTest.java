package org.apache.catalina.connector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConnectorTest {
    @Test
    void queuesRequestsWhenAllWorkersAreBusy() throws IOException {
        final var port = availablePort();
        final var connector = new Connector(port, 100, 1);
        connector.start();

        try (final var first = new Socket("localhost", port);
             final var second = new Socket("localhost", port)) {
            second.setSoTimeout(300);
            sendRequest(second);

            assertThatThrownBy(() -> second.getInputStream().read())
                    .isInstanceOf(SocketTimeoutException.class);

            sendRequest(first);
            first.setSoTimeout(3000);
            assertThat(response(first)).startsWith("HTTP/1.1 200 OK");

            second.setSoTimeout(3000);
            assertThat(response(second)).startsWith("HTTP/1.1 200 OK");
        } finally {
            connector.stop();
        }
    }

    @Test
    void rejectsNonPositiveMaxThreads() {
        assertThatThrownBy(() -> new Connector(8080, 100, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("maxThreads must be positive");
    }

    private int availablePort() throws IOException {
        try (final var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private void sendRequest(final Socket socket) throws IOException {
        socket.getOutputStream().write("GET / HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        socket.getOutputStream().flush();
    }

    private String response(final Socket socket) throws IOException {
        return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
