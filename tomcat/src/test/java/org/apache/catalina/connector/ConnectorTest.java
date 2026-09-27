package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    private static final int TEST_PORT = 19999;
    private static final int REJECT_TEST_PORT = 19998;
    private static final int MAX_THREADS = 4;
    private static final int REQUEST_COUNT = 20;

    // acceptCount(대기열 용량)를 REQUEST_COUNT보다 넉넉하게 줘서 동시 요청이 거절되지 않게 한다.
    private final Connector connector = new Connector(TEST_PORT, REQUEST_COUNT, MAX_THREADS);

    @AfterEach
    void tearDown() {
        connector.stop();
    }

    @Test
    void 스레드_풀_크기보다_많은_요청이_와도_모두_처리한다() throws Exception {
        connector.start();

        final ExecutorService clientPool = Executors.newFixedThreadPool(REQUEST_COUNT);
        try {
            final List<Callable<String>> requests = List.copyOf(
                    java.util.stream.IntStream.range(0, REQUEST_COUNT)
                            .<Callable<String>>mapToObj(i -> this::sendHelloRequest)
                            .toList());

            final List<Future<String>> results = clientPool.invokeAll(requests, 10, TimeUnit.SECONDS);

            for (final Future<String> result : results) {
                assertThat(result.get()).startsWith("HTTP/1.1 200 OK");
            }
        } finally {
            clientPool.shutdown();
        }
    }

    @Test
    void 스레드_풀과_대기열이_가득_차면_503을_응답하고_연결을_닫는다() throws IOException {
        // acceptCount(2)는 더 이상 100으로 올림되지 않고 대기열 용량(2)에 그대로 반영된다.
        final Connector smallConnector = new Connector(REJECT_TEST_PORT, 2, 2);
        smallConnector.start();

        final List<Socket> openSockets = new ArrayList<>();
        try {
            String statusLine = null;
            // 스레드(2) + 대기열(2) = 4개까지는 응답 없이 대기하고, 5번째부터 503을 받는다.
            for (int attempt = 0; attempt < 20 && statusLine == null; attempt++) {
                final Socket socket = new Socket("localhost", REJECT_TEST_PORT);
                socket.setSoTimeout(2000);
                openSockets.add(socket);

                try {
                    final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    statusLine = reader.readLine();
                } catch (SocketTimeoutException e) {
                    // 아직 거절되지 않고 스레드나 대기열에서 요청을 기다리는 중이다.
                }
            }

            assertThat(statusLine).startsWith("HTTP/1.1 503 Service Unavailable");
        } finally {
            for (final Socket socket : openSockets) {
                socket.close();
            }
            smallConnector.stop();
        }
    }

    private String sendHelloRequest() throws IOException {
        try (final Socket socket = new Socket("localhost", TEST_PORT)) {
            final OutputStream outputStream = socket.getOutputStream();
            outputStream.write("GET / HTTP/1.1 \r\nHost: localhost\r\n\r\n".getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            final BufferedReader reader =
                    new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            return reader.readLine();
        }
    }
}
