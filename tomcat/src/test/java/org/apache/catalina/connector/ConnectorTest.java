package org.apache.catalina.connector;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.routing.RequestMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    @DisplayName("요청마다 새로운 스레드를 생성하는 게 아니라 스레드 풀을 활용한다.")
    @Test
    void process() throws IOException {
        // given
        final Set<Thread> usedThreads = ConcurrentHashMap.newKeySet();

        final Controller controller = (request, response) -> {
            usedThreads.add(Thread.currentThread());
        };

        final int port = 8080;
        final int acceptCount = 10;
        final RequestMapping requestMapping = new RequestMapping(controller);
        final int maxThreads = 1;

        final Connector connector = new Connector(port, acceptCount, requestMapping, maxThreads);

        final String httpRequest = "GET / HTTP/1.1\r\n"
                + "Host: localhost:" + port + "\r\n"
                + "\r\n";

        // when
        connector.start();
        try {
            for (int i = 0; i < 2; i++) {
                try (Socket client = new Socket("localhost", port)) {
                    client.setSoTimeout(3000);

                    var output = client.getOutputStream();
                    output.write(httpRequest.getBytes(StandardCharsets.UTF_8));
                    output.flush();

                    byte[] response = client.getInputStream().readAllBytes();
                    assertThat(new String(response, StandardCharsets.UTF_8))
                            .startsWith("HTTP/1.1 200");
                }
            }
        } finally {
            connector.stop();
        }

        // then
        assertThat(usedThreads).hasSize(1);
    }
}
