package org.apache.catalina.connector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    @Test
    void waitsForAnInFlightRequestToFinishBeforeStopping() throws Exception {
        final var port = availablePort();
        final var connector = new Connector(port, 100, 1);
        connector.start();

        try (final var connection = new Socket("localhost", port);
             final var executor = Executors.newSingleThreadExecutor()) {
            sendIncompleteRequest(connection);
            Thread.sleep(200);

            final var stop = executor.submit(connector::stop);
            Thread.sleep(200);

            assertThat(stop.isDone()).isFalse();

            connection.close();
            stop.get(3, TimeUnit.SECONDS);
        } finally {
            connector.stop();
        }
    }

    @Test
    void closesInFlightAndQueuedConnectionsAfterShutdownTimeout() throws Exception {
        final var port = availablePort();
        final var connector = new Connector(port, 100, 1);
        connector.start();

        try (final var inFlight = new Socket("localhost", port);
             final var queued = new Socket("localhost", port)) {
            sendIncompleteRequest(inFlight);
            sendIncompleteRequest(queued);
            Thread.sleep(200);

            connector.stop();

            assertThat(connectionIsClosed(inFlight)).isTrue();
            assertThat(connectionIsClosed(queued)).isTrue();
        } finally {
            connector.stop();
        }
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

    private void sendIncompleteRequest(final Socket socket) throws IOException {
        socket.getOutputStream().write("GET / HTTP/1.1\r\n".getBytes(StandardCharsets.UTF_8));
        socket.getOutputStream().flush();
    }

    private boolean connectionIsClosed(final Socket socket) throws IOException {
        socket.setSoTimeout(3000);
        try {
            return socket.getInputStream().read() == -1;
        } catch (SocketException e) {
            return true;
        }
    }

    private String response(final Socket socket) throws IOException {
        return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
