package org.apache.catalina.connector;

import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.RequestMapping;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    @Test
    void 작업_스레드와_대기열이_모두_차면_503을_응답한다() throws Exception {
        final CountDownLatch requestStarted = new CountDownLatch(1);
        final CountDownLatch releaseRequest = new CountDownLatch(1);
        final Controller blockingController = (request, response) -> {
            requestStarted.countDown();
            releaseRequest.await();
        };
        final RequestMapping requestMapping = new RequestMapping(
                Map.of("/block", blockingController), blockingController);
        final ThreadPoolExecutor executor = Connector.createExecutor(1, 1);

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            final Connector connector = new Connector(serverSocket, executor, requestMapping);
            connector.start();

            try (Socket first = new Socket("127.0.0.1", serverSocket.getLocalPort());
                 Socket second = new Socket("127.0.0.1", serverSocket.getLocalPort())) {
                sendRequest(first);
                assertThat(requestStarted.await(5, TimeUnit.SECONDS)).isTrue();

                sendRequest(second);
                awaitQueuedTask(executor);

                try (Socket third = new Socket("127.0.0.1", serverSocket.getLocalPort())) {
                    third.setSoTimeout(5_000);
                    sendRequest(third);

                    final String response = new String(third.getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8);
                    assertThat(response).isEqualTo(String.join("\r\n",
                            "HTTP/1.1 503 Service Unavailable",
                            "Connection: close",
                            "Content-Length: 0",
                            "",
                            ""));
                }

                releaseRequest.countDown();
                first.setSoTimeout(5_000);
                second.setSoTimeout(5_000);
                assertThat(new String(first.getInputStream().readAllBytes(), StandardCharsets.UTF_8))
                        .startsWith("HTTP/1.1 200 OK\r\n");
                assertThat(new String(second.getInputStream().readAllBytes(), StandardCharsets.UTF_8))
                        .startsWith("HTTP/1.1 200 OK\r\n");
            } finally {
                releaseRequest.countDown();
                connector.stop();
                assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
            }
        }
    }

    private void sendRequest(final Socket socket) throws IOException {
        socket.getOutputStream().write("GET /block HTTP/1.1\r\nHost: localhost\r\n\r\n"
                .getBytes(StandardCharsets.UTF_8));
        socket.getOutputStream().flush();
    }

    private void awaitQueuedTask(final ThreadPoolExecutor executor) throws InterruptedException {
        final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (executor.getQueue().size() != 1 && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        assertThat(executor.getQueue()).hasSize(1);
    }
}
