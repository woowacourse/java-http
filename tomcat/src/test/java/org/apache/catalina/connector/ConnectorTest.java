package org.apache.catalina.connector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    @Test
    @Timeout(30)
    @DisplayName("400개 연결 중 250개 실행, 100개 대기, 50개 거절")
    void limitsConnections() throws Exception {
        final var busyWorkers = new CountDownLatch(250);
        final var releaseWorkers = new CountDownLatch(1);
        final var startedRequests = new AtomicInteger();
        final var connections = new ArrayList<Socket>();
        final var request = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes(StandardCharsets.UTF_8);

        try (final var serverSocket = new ServerSocket(0, 100, InetAddress.getLoopbackAddress())) {
            final var connector = new Connector(serverSocket, 250, (req, res) -> {
                startedRequests.incrementAndGet();
                busyWorkers.countDown();
                releaseWorkers.await(); // 확인이 끝날 때까지 작업 스레드를 점유한다.
            });
            connector.start();

            try {
                for (int i = 0; i < 400; i++) {
                    final var connection = new Socket();
                    connections.add(connection);
                    connection.connect(new InetSocketAddress(serverSocket.getInetAddress(),
                            serverSocket.getLocalPort()), 5_000);
                    connection.setSoTimeout(5_000);
                    if (i < 350) {
                        connection.getOutputStream().write(request);
                    }
                }
                assertThat(busyWorkers.await(5, SECONDS)).isTrue();

                // 초과 50개는 TCP 연결 후 서버가 곧바로 닫는다.
                for (final var connection : connections.subList(350, 400)) {
                    assertThat(connection.getInputStream().read()).isEqualTo(-1);
                }
                // 수락된 350개 중 250개만 실행을 시작했으므로 나머지 100개는 대기 중이다.
                assertThat(startedRequests.get()).isEqualTo(250);

                releaseWorkers.countDown();
                for (final var connection : connections.subList(0, 350)) {
                    final var response = new String(connection.getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8);
                    assertThat(response).startsWith("HTTP/1.1 200 OK");
                }
                assertThat(startedRequests.get()).isEqualTo(350);
            } finally {
                releaseWorkers.countDown();
                connector.stop();
                for (final var connection : connections) {
                    connection.close();
                }
            }
        }
    }
}
