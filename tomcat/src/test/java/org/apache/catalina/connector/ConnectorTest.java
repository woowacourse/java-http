package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @DisplayName("서버를 종료하면 더 이상 클라이언트를 받을 수 없다")
    @Test
    void stop() {
        Connector connector = new Connector();
        connector.start();
        connector.stop();

        assertThatThrownBy(() -> {
            try (Socket clientSocket = new Socket("localhost", 8080)) {}
        }).isInstanceOf(ConnectException.class);
    }

    @DisplayName("스레드 풀의 크기는 250이다")
    @Test
    void testServerStop() {
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(250);
        for (int i = 0; i < 350; i++) {
            executor.submit(threadSleep());
        }
        final int expectedPoolSize = 250;
        final int expectedQueueSize = 100;

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
    }

    private Runnable threadSleep() {
        return () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
