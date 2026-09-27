package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.response.StatusCode;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Test
    void 헤더를_끝까지_보내지_않은_연결은_읽기_제한_시간_후_닫힌다() throws Exception {
        int port;
        try (var serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
        }

        var mapping = new RequestMapping(Map.of("/", (request, response) ->
                response.setStatusCode(StatusCode.OK)), (request, response) -> {
        });
        var connector = new Connector(mapping, port, 100, 1, 1, 200);
        connector.start();
        try {
            try (var stalled = new Socket("localhost", port)) {
                stalled.setSoTimeout(2_000);
                stalled.getOutputStream().write("GET / HTTP/1.1\r\nHost: localhost\r\n"
                        .getBytes(StandardCharsets.UTF_8));
                stalled.getOutputStream().flush();

                assertThat(stalled.getInputStream().read()).isEqualTo(-1);
            }

            try (var next = new Socket("localhost", port)) {
                next.setSoTimeout(2_000);
                next.getOutputStream().write("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n"
                        .getBytes(StandardCharsets.UTF_8));
                next.getOutputStream().flush();

                var reader = new BufferedReader(new InputStreamReader(next.getInputStream(), StandardCharsets.UTF_8));
                assertThat(reader.readLine()).startsWith("HTTP/1.1 200 OK");
            }
        } finally {
            connector.stop();
        }
    }
}
