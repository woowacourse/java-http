package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.Socket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    private Connector connector;

    @BeforeEach
    void setUp() {
        connector = new Connector(8080, 100, 10);
    }

    @AfterEach
    void shutDown() {
        connector.stop();
    }

    @DisplayName("서버가 시작되고 클라이언트가 연결을 성공해야 한다")
    @Test
    void connector_StartsAndAcceptsConnections() throws IOException {
        // given
        connector.start();

        // then
        Socket clientSocket = new Socket("localhost", 8080);

        assertThat(clientSocket.isConnected()).isTrue();
    }

    @DisplayName("서버가 최대 스레드 내에서 여러 클라이언트 연결을 성공해야 한다")
    @Test
    void multipleConnectionsWithMaxThreadLimit() throws IOException {
        // given & when
        for (int i = 0; i < 10; i++) {
            try (Socket clientSocket = new Socket("localhost", 8080)) {
                // then
                assertThat(clientSocket.isConnected()).isTrue();
            }
        }
    }
}
