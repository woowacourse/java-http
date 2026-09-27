package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
    private static final int MAX_THREADS = 4;
    private static final int REQUEST_COUNT = 20;

    private final Connector connector = new Connector(TEST_PORT, MAX_THREADS, MAX_THREADS);

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
