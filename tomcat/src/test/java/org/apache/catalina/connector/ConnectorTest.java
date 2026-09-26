package org.apache.catalina.connector;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConnectorTest {

    @Test
    void processesAcceptedConnection() throws IOException {
        // given
        final int port = availablePort();
        final Connector connector = new Connector(port, 100, 1, 1);
        connector.start();

        try (final Socket connection = new Socket("localhost", port)) {
            connection.setSoTimeout(3_000);

            // when
            sendRequest(connection);
            final String response = readResponse(connection);

            // then
            assertThat(response).startsWith("HTTP/1.1 200 OK");
        } finally {
            connector.stop();
        }
    }

    @Test
    void waitsForAvailableWorkerAndRejectsWhenQueueIsFull() throws Exception {
        // given
        final int port = availablePort();
        final Connector connector = new Connector(port, 100, 1, 1);
        connector.start();

        try (final Socket first = new Socket("localhost", port);
             final Socket second = new Socket("localhost", port)) {
            first.setSoTimeout(3_000);
            second.setSoTimeout(300);
            first.getOutputStream().write("GET / HTTP/1.1\r\n".getBytes(StandardCharsets.UTF_8));
            first.getOutputStream().flush();
            awaitActiveWorker(connector);

            // when
            sendRequest(second);
            awaitQueuedTask(connector);

            // then
            assertThatThrownBy(() -> second.getInputStream().read())
                    .isInstanceOf(SocketTimeoutException.class);
            try (final Socket third = new Socket("localhost", port)) {
                third.setSoTimeout(3_000);
                assertThat(third.getInputStream().read()).isEqualTo(-1);
            }

            first.getOutputStream().write("\r\n".getBytes(StandardCharsets.UTF_8));
            first.getOutputStream().flush();
            assertThat(readResponse(first)).startsWith("HTTP/1.1 200 OK");

            second.setSoTimeout(3_000);
            assertThat(readResponse(second)).startsWith("HTTP/1.1 200 OK");
        } finally {
            connector.stop();
        }
    }

    @Test
    void stopShutsDownWorkerPool() throws Exception {
        // given
        final Connector connector = new Connector(availablePort(), 100, 1, 1);

        // when
        connector.stop();

        // then
        assertThat(executorOf(connector).isTerminated()).isTrue();
    }

    @Test
    void stopWaitsForRunningRequest() throws Exception {
        // given
        final int port = availablePort();
        final Connector connector = new Connector(port, 100, 1, 1);
        connector.start();

        try (final Socket connection = new Socket("localhost", port)) {
            connection.setSoTimeout(3_000);
            connection.getOutputStream().write("GET / HTTP/1.1\r\n".getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();
            awaitActiveWorker(connector);

            // when
            final Thread stopper = new Thread(connector::stop);
            stopper.start();
            stopper.join(300);

            // then
            assertThat(stopper.isAlive()).isTrue();
            assertThat(executorOf(connector).isTerminated()).isFalse();

            connection.getOutputStream().write("\r\n".getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();
            assertThat(readResponse(connection)).startsWith("HTTP/1.1 200 OK");

            stopper.join(3_000);
            assertThat(stopper.isAlive()).isFalse();
            assertThat(executorOf(connector).isTerminated()).isTrue();
        }
    }

    @Test
    void invalidWorkerConfigDoesNotBindPort() throws IOException {
        // given
        final int port = availablePort();

        // when & then
        assertThatThrownBy(() -> new Connector(port, 100, 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Connector(port, 100, 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
        try (final ServerSocket socket = new ServerSocket(port)) {
            assertThat(socket.getLocalPort()).isEqualTo(port);
        }
    }

    private int availablePort() throws IOException {
        try (final ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private void sendRequest(final Socket socket) throws IOException {
        socket.getOutputStream().write("GET / HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        socket.getOutputStream().flush();
    }

    private String readResponse(final Socket socket) throws IOException {
        return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void awaitActiveWorker(final Connector connector) throws Exception {
        final ThreadPoolExecutor executor = executorOf(connector);
        final long deadline = System.nanoTime() + Duration.ofSeconds(3).toNanos();

        while (executor.getActiveCount() == 0 && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        assertThat(executor.getActiveCount()).isEqualTo(1);
    }

    private void awaitQueuedTask(final Connector connector) throws Exception {
        final ThreadPoolExecutor executor = executorOf(connector);
        final long deadline = System.nanoTime() + Duration.ofSeconds(3).toNanos();

        while (executor.getQueue().isEmpty() && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        assertThat(executor.getQueue()).hasSize(1);
    }

    private ThreadPoolExecutor executorOf(final Connector connector) throws Exception {
        final Field field = Connector.class.getDeclaredField("executorService");
        field.setAccessible(true);
        return (ThreadPoolExecutor) field.get(connector);
    }
}
