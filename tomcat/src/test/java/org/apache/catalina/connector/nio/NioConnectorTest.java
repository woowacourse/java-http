package org.apache.catalina.connector.nio;

import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class NioConnectorTest {

    @Test
    void NIO로_HTTP_요청을_처리하고_응답한_뒤_연결을_종료한다() throws Exception {
        final var connector = new NioConnector(0, 100, 2, 2);
        connector.start();

        try (final var client = new Socket("localhost", connector.getLocalPort())) {
            client.setSoTimeout(3_000);
            client.getOutputStream().write(("GET /index.html HTTP/1.1\r\n"
                    + "Host: localhost\r\n"
                    + "Connection: close\r\n\r\n").getBytes(StandardCharsets.ISO_8859_1));

            final String response = new String(client.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            assertThat(response).startsWith("HTTP/1.1 200 OK");
            assertThat(response).contains("<!DOCTYPE html>");
        } finally {
            connector.stop();
        }
    }
}
