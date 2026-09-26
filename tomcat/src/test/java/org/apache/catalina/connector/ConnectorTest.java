package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Connector 스레드 풀")
class ConnectorTest {

    @Test
    @DisplayName("스레드 풀과 작업 큐가 가득 차면 추가 연결을 종료한다")
    void closesConnectionWhenThreadPoolAndQueueAreFull() throws IOException {
        final int port = findAvailablePort();
        final var connector = new Connector(port, 100, 1, 1);
        connector.start();

        try (var running = connect(port);
             var waiting = connect(port);
             var rejected = connect(port)) {
            rejected.setSoTimeout(2_000);

            assertThat(rejected.getInputStream().read()).isEqualTo(-1);

            sendRequest(running);
            sendRequest(waiting);
            assertThat(readResponse(running)).startsWith("HTTP/1.1 200 OK");
            assertThat(readResponse(waiting)).startsWith("HTTP/1.1 200 OK");
        } finally {
            connector.stop();
        }
    }

    private Socket connect(final int port) throws IOException {
        return new Socket("127.0.0.1", port);
    }

    private void sendRequest(final Socket socket) throws IOException {
        final byte[] request = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n"
                .getBytes(StandardCharsets.UTF_8);
        socket.getOutputStream().write(request);
        socket.getOutputStream().flush();
    }

    private String readResponse(final Socket socket) throws IOException {
        socket.setSoTimeout(2_000);
        return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private int findAvailablePort() throws IOException {
        try (var serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
