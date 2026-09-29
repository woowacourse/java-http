package org.apache.catalina.connector;

import org.apache.coyote.http11.ControllerResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConnectorTest {

    @Test
    @Timeout(15)
    void 요청_두_개를_처리하는_동안_세_번째_요청은_기다린다() throws Exception {
        // given
        AtomicInteger activeRequests = new AtomicInteger();
        AtomicInteger maxActiveRequests = new AtomicInteger();
        AtomicInteger startedRequests = new AtomicInteger();
        CountDownLatch firstTwoStarted = new CountDownLatch(2);
        CountDownLatch thirdStarted = new CountDownLatch(1);
        CountDownLatch completedRequests = new CountDownLatch(3);
        CountDownLatch releaseRequests = new CountDownLatch(1);
        // 앞 두 요청 멈추기 (새로 들어온 스레드가 기다리는지 확인하기 위함)
        ControllerResolver mapping = request -> Optional.of((req, response) -> {
            final int active = activeRequests.incrementAndGet();
            maxActiveRequests.accumulateAndGet(active, Math::max);
            int started = startedRequests.incrementAndGet();
            if (started <= 2) {
                firstTwoStarted.countDown();
            } else {
                thirdStarted.countDown();
            }
            try {
                releaseRequests.await();
                response.sendRedirect("/index.html");
            } finally {
                activeRequests.decrementAndGet();
                completedRequests.countDown();
            }
        });

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            Connector connector = new Connector(2, serverSocket, mapping);
            connector.start();
            try (Socket first = sendRequest(serverSocket.getLocalPort());
                 Socket second = sendRequest(serverSocket.getLocalPort())) {
                assertThat(firstTwoStarted.await(5, TimeUnit.SECONDS)).isTrue();

                // when
                try (Socket third = sendRequest(serverSocket.getLocalPort())) {
                    // then
                    assertThat(thirdStarted.await(200, TimeUnit.MILLISECONDS)).isFalse();
                    // 앞 요청이 끝나면 세 번째 요청이 시작
                    releaseRequests.countDown();
                    assertThat(thirdStarted.await(5, TimeUnit.SECONDS)).isTrue();
                    assertThat(completedRequests.await(5, TimeUnit.SECONDS)).isTrue();
                    assertThat(maxActiveRequests.get()).isEqualTo(2);
                }
            } finally {
                releaseRequests.countDown();
                connector.stop();
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 스레드_수를_0_이하로_설정하면_Connector_생성에_실패한다(int maxThreads) {
        ControllerResolver mapping = request -> Optional.empty();

        assertThatThrownBy(() -> new Connector(0, 100, maxThreads, mapping))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 작업_대기열_크기를_0_이하로_설정하면_Connector_생성에_실패한다(int maxQueuedRequests) {
        ControllerResolver mapping = request -> Optional.empty();

        assertThatThrownBy(() -> new Connector(0, 100, 2, maxQueuedRequests, mapping))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Timeout(15)
    void 작업_스레드와_대기열이_가득_차면_새_요청에_503을_응답한다() throws Exception {
        // given
        CountDownLatch firstTwoStarted = new CountDownLatch(2);
        CountDownLatch releaseRequests = new CountDownLatch(1);
        ControllerResolver mapping = request -> Optional.of((req, response) -> {
            firstTwoStarted.countDown();
            releaseRequests.await();
            response.sendRedirect("/index.html");
        });

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            Connector connector = new Connector(2, 1, serverSocket, mapping);
            connector.start();
            try (Socket first = sendRequest(serverSocket.getLocalPort());
                 Socket second = sendRequest(serverSocket.getLocalPort())) {
                assertThat(firstTwoStarted.await(5, TimeUnit.SECONDS)).isTrue();

                // when
                try (Socket waiting = sendRequest(serverSocket.getLocalPort());
                     Socket rejected = sendRequest(serverSocket.getLocalPort())) {
                    rejected.setSoTimeout(5_000);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(rejected.getInputStream(), StandardCharsets.UTF_8));
                    String statusLine = reader.readLine();

                    // then
                    assertThat(statusLine).startsWith("HTTP/1.1 503 Service Unavailable");
                    releaseRequests.countDown();
                    waiting.setSoTimeout(5_000);
                    String waitingResponse = new String(waiting.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    assertThat(waitingResponse).startsWith("HTTP/1.1 302 Found");
                }
            } finally {
                releaseRequests.countDown();
                connector.stop();
            }
        }
    }

    @Test
    @Timeout(15)
    void 요청_20개가_한꺼번에_와도_스레드_4개로_모두_응답한다() throws Exception {
        // given
        AtomicInteger activeRequests = new AtomicInteger();
        AtomicInteger maxActiveRequests = new AtomicInteger();
        CountDownLatch firstFourStarted = new CountDownLatch(4);
        CountDownLatch releaseRequests = new CountDownLatch(1);
        // 첫 4개의 요청 멈춰서 나머지 요청이 기다리게 만듦
        ControllerResolver mapping = request -> Optional.of((req, response) -> {
            int active = activeRequests.incrementAndGet();
            maxActiveRequests.accumulateAndGet(active, Math::max);
            firstFourStarted.countDown();
            try {
                releaseRequests.await();
                response.sendRedirect("/index.html");
            } finally {
                activeRequests.decrementAndGet();
            }
        });

        List<Socket> connections = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            Connector connector = new Connector(4, serverSocket, mapping);
            connector.start();
            try {
                for (int index = 0; index < 20; index++) {
                    connections.add(sendRequest(serverSocket.getLocalPort()));
                }

                // when
                assertThat(firstFourStarted.await(5, TimeUnit.SECONDS)).isTrue();
                releaseRequests.countDown();

                // then
                for (Socket connection : connections) {
                    connection.setSoTimeout(5_000);
                    String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    assertThat(response).startsWith("HTTP/1.1 302 Found");
                }
                assertThat(maxActiveRequests.get()).isEqualTo(4);
            } finally {
                releaseRequests.countDown();
                connector.stop();
                for (Socket connection : connections) {
                    connection.close();
                }
            }
        }
    }

    private Socket sendRequest(int port) throws IOException {
        Socket socket = new Socket("127.0.0.1", port);
        String request = "GET /login HTTP/1.1\r\nHost: localhost\r\n\r\n";
        socket.getOutputStream().write(request.getBytes(StandardCharsets.UTF_8));
        socket.getOutputStream().flush();
        return socket;
    }
}
