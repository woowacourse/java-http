package org.apache.catalina.connector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("커넥터")
class ConnectorTest {

    @Nested
    @DisplayName("종료하면")
    class StopTest {

        private static final long SHUTDOWN_TIMEOUT_SECONDS = 5L;

        private ServerSocket serverSocket;
        private ExecutorService executorService;
        private Connector connector;

        @BeforeEach
        void setUp() {
            serverSocket = mock(ServerSocket.class);
            executorService = mock(ExecutorService.class);
            when(executorService.shutdownNow()).thenReturn(List.of());
            connector = new Connector(serverSocket, executorService);
        }

        @AfterEach
        void clearInterruptStatus() {
            Thread.interrupted();
        }

        @Test
        @DisplayName("새 연결을 차단하고 진행 중인 작업의 종료를 기다린다")
        void waitsForRunningTasks() throws IOException, InterruptedException {
            when(executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                    .thenReturn(true);

            connector.stop();

            final var ordered = inOrder(serverSocket, executorService);
            ordered.verify(serverSocket).close();
            ordered.verify(executorService).shutdown();
            ordered.verify(executorService)
                    .awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            verify(executorService, never()).shutdownNow();
        }

        @Test
        @DisplayName("제한 시간 안에 끝나지 않은 작업에는 인터럽트를 요청한다")
        void interruptsTasksAfterTimeout() throws InterruptedException {
            when(executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                    .thenReturn(false, true);

            connector.stop();

            verify(executorService).shutdownNow();
            verify(executorService, times(2))
                    .awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("종료를 기다리던 스레드가 인터럽트되면 인터럽트 상태를 복구한다")
        void restoresInterruptStatus() throws InterruptedException {
            when(executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                    .thenThrow(InterruptedException.class);

            connector.stop();

            verify(executorService).shutdownNow();
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
        }
    }
}
