package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.coyote.http11.RequestMapping;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Test
    void waitsForAvailableThreadWhenAllThreadsAreBusy() throws Exception {
        // given
        CountDownLatch firstStarted = new CountDownLatch(1);
        CountDownLatch secondStarted = new CountDownLatch(1);
        CountDownLatch releaseFirst = new CountDownLatch(1);
        AtomicInteger requestCount = new AtomicInteger();
        int port = availablePort();
        Connector connector = new Connector(port, 100, 1, session -> new RequestMapping(Map.of(
                "/", (request, response) -> {
                    if (requestCount.incrementAndGet() == 1) {
                        firstStarted.countDown();
                        releaseFirst.await(5, TimeUnit.SECONDS);
                    } else {
                        secondStarted.countDown();
                    }
                    response.send("text/plain", "done");
                })));

        connector.start();
        try (Socket first = new Socket("localhost", port);
             Socket second = new Socket("localhost", port)) {
            // when
            sendRequest(first);
            assertThat(firstStarted.await(5, TimeUnit.SECONDS)).isTrue();
            sendRequest(second);

            // then
            assertThat(secondStarted.await(300, TimeUnit.MILLISECONDS)).isFalse();
            releaseFirst.countDown();
            assertThat(secondStarted.await(5, TimeUnit.SECONDS)).isTrue();
        } finally {
            releaseFirst.countDown();
            connector.stop();
        }
    }

    private int availablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private void sendRequest(Socket socket) throws IOException {
        socket.getOutputStream().write("GET / HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8));
    }
}
