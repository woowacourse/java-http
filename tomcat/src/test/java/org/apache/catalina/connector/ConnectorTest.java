package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.controller.RequestMapping;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    private static final int QUEUE_CAPACITY = 100;

    @Test
    void 풀과_큐가_가득_차면_추가_연결을_닫는다() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        RequestMapping requestMapping = new RequestMapping((request, response) -> {
            started.countDown();
            release.await();
            request.getSession().invalidate();
        });

        int port = findAvailablePort();
        Connector connector = new Connector(port, 100, requestMapping, 1);
        List<Socket> connections = new ArrayList<>();

        try {
            connector.start();

            Socket first = new Socket("127.0.0.1", port);
            connections.add(first);
            first.getOutputStream().write("GET / HTTP/1.1\r\n\r\n".getBytes());
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

            for (int i = 0; i < QUEUE_CAPACITY; i++) {
                connections.add(new Socket("127.0.0.1", port));
            }
            Socket lastQueued = connections.getLast();

            Socket rejected = new Socket("127.0.0.1", port);
            connections.add(rejected);
            rejected.setSoTimeout(5_000);
            assertThat(rejected.getInputStream().read()).isEqualTo(-1);

            lastQueued.setSoTimeout(500);
            assertThatThrownBy(() -> lastQueued.getInputStream().read())
                    .isInstanceOf(SocketTimeoutException.class);
        } finally {
            release.countDown();
            for (Socket connection : connections) {
                connection.close();
            }
            connector.stop();
        }
    }

    private int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}