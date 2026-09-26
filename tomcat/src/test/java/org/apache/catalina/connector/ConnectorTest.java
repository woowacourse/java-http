package org.apache.catalina.connector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    @Test
    @DisplayName("maxThreads 크기의 스레드 풀을 생성한다.")
    void createThreadPoolWithMaxThreads() throws Exception {
        // given
        Connector connector = new Connector(findAvailablePort(), 100, 7);

        try {
            // when
            ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorService(connector);

            // then
            assertThat(executorService.getCorePoolSize()).isEqualTo(7);
            assertThat(executorService.getMaximumPoolSize()).isEqualTo(7);
        } finally {
            connector.stop();
        }
    }

    @Test
    @DisplayName("Connector를 종료하면 스레드 풀도 종료한다.")
    void shutdownThreadPoolWhenStop() throws Exception {
        // given
        Connector connector = new Connector(findAvailablePort(), 100, 7);
        ExecutorService executorService = executorService(connector);

        // when
        connector.stop();

        // then
        assertThat(executorService.isShutdown()).isTrue();
    }

    private ExecutorService executorService(Connector connector) throws Exception {
        Field field = Connector.class.getDeclaredField("executorService");
        field.setAccessible(true);

        return (ExecutorService) field.get(connector);
    }

    private int findAvailablePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
