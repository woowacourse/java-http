package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.Manager;
import org.apache.coyote.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("커넥터 테스트")
class ConnectorTest {

    private Connector connector;
    private Manager manager;
    private final int port = 1234;

    @BeforeEach
    void setUp() {
        manager = new SessionManager();
        connector = new Connector(manager);
    }

    @AfterEach
    void tearDown() {
        connector.stop();
    }

    @DisplayName("서버를 시작하면 클라이언트가 정상적으로 연결되고 요청을 처리할 수 있어야 한다.")
    @Test
    void testClientConnection() throws Exception {
        // given
        connector.start();

        // when
        String response = sendHttpRequest("GET /index.html HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");

        // then
        assertThat(response).contains("HTTP/1.1 200 OK");
    }

    @DisplayName("서버를 종료하면 더 이상 클라이언트를 받을 수 없어야 한다.")
    @Test
    void testServerStop() {
        // given
        connector.start();

        // when
        connector.stop();

        // then
        Assertions.assertThrows(Exception.class, () -> {
            try (Socket clientSocket = new Socket("localhost", port)) {
            }
        });
    }

    @DisplayName("서버가 다중 클라이언트의 요청을 처리할 수 있어야 한다.")
    @Test
    void testMultipleClientConnections() throws Exception {
        // given
        int maxThreads = 5;
        connector.start();

        ExecutorService clientThreads = Executors.newFixedThreadPool(maxThreads);

        // when
        for (int i = 0; i < maxThreads; i++) {
            clientThreads.execute(() -> {
                try {
                    String response = sendHttpRequest("GET / HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
                    // then
                    assertThat(response).contains("HTTP/1.1 200 OK");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        clientThreads.shutdown();
        clientThreads.awaitTermination(10, TimeUnit.SECONDS);
    }

    private String sendHttpRequest(String request) throws Exception {
        try (Socket clientSocket = new Socket("localhost", port);
             OutputStream outputStream = clientSocket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            // Send request
            outputStream.write(request.getBytes());
            outputStream.flush();

            // Read response
            String responseLine;
            StringBuilder response = new StringBuilder();
            while ((responseLine = reader.readLine()) != null) {
                response.append(responseLine).append("\n");
            }
            return response.toString();
        }
    }
}
