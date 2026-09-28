package org.apache.catalina.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.http11.session.HttpSessionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    private static final int ACCEPT_COUNT = 100;
    private static final long TIMEOUT_SECONDS = 5;

    private final AtomicInteger runningCount = new AtomicInteger();
    private final AtomicInteger maxRunningCount = new AtomicInteger();
    private final CountDownLatch release = new CountDownLatch(1);
    private final ExecutorService clients = Executors.newCachedThreadPool();

    private Connector connector;

    @AfterEach
    void tearDown() {
        release.countDown();
        clients.shutdownNow();
        if (connector != null) {
            connector.stop();
        }
    }

    @Test
    void maxThreads만큼의_요청을_동시에_처리한다() throws Exception {
        // given
        final int maxThreads = 3;
        final CountDownLatch entered = new CountDownLatch(maxThreads);
        final int port = startConnector(maxThreads, entered);

        // when
        final List<Future<String>> responses = sendRequests(port, maxThreads);

        // then
        assertThat(entered.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();
        assertThat(runningCount.get()).isEqualTo(maxThreads);

        release.countDown();
        for (final Future<String> response : responses) {
            assertThat(response.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)).contains("HTTP/1.1 200 OK");
        }
    }

    @Test
    void 모든_스레드가_사용_중이면_초과_요청은_대기했다가_처리된다() throws Exception {
        // given
        final int maxThreads = 2;
        final int requestCount = 5;
        final CountDownLatch entered = new CountDownLatch(maxThreads);
        final int port = startConnector(maxThreads, entered);

        // when
        final List<Future<String>> responses = sendRequests(port, requestCount);

        // then
        assertThat(entered.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();
        TimeUnit.MILLISECONDS.sleep(500);
        assertThat(runningCount.get()).isEqualTo(maxThreads);

        release.countDown();
        for (final Future<String> response : responses) {
            assertThat(response.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)).contains("HTTP/1.1 200 OK");
        }
        assertThat(maxRunningCount.get()).isEqualTo(maxThreads);
    }

    private int startConnector(final int maxThreads, final CountDownLatch entered) throws IOException {
        final int port = findFreePort();
        final Controller blockingController = (request, response) -> {
            maxRunningCount.accumulateAndGet(runningCount.incrementAndGet(), Math::max);
            entered.countDown();
            try {
                release.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                response.ok("text/plain", "ok".getBytes(StandardCharsets.UTF_8));
            } finally {
                runningCount.decrementAndGet();
            }
        };
        connector = new Connector(
                port,
                ACCEPT_COUNT,
                maxThreads,
                new RequestMapping(Map.of("/block", blockingController)),
                blockingController,
                new HttpSessionHandler(SessionManager.getInstance())
        );
        connector.start();
        return port;
    }

    private int findFreePort() throws IOException {
        try (final ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private List<Future<String>> sendRequests(final int port, final int count) {
        final List<Future<String>> responses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            responses.add(clients.submit(() -> sendRequest(port)));
        }
        return responses;
    }

    private String sendRequest(final int port) throws IOException {
        final String request = String.join("\r\n",
                "GET /block HTTP/1.1",
                "Host: localhost:" + port,
                "",
                "");
        try (final Socket socket = new Socket("localhost", port)) {
            final OutputStream outputStream = socket.getOutputStream();
            outputStream.write(request.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            final InputStream inputStream = socket.getInputStream();
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
