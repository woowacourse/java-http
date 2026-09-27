package org.apache.catalina.connector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    @Test
    @DisplayName("작업 큐가 가득 차면 연결을 닫고, 여유가 생기면 다시 요청을 처리한다.")
    void rejectConnectionWhenQueueIsFullAndRecover() throws Exception {
        int port = findAvailablePort();
        Connector connector = new Connector(port, 100, 1);
        ExecutorService executor = executorService(connector);
        CountDownLatch workerStarted = new CountDownLatch(1);
        CountDownLatch releaseWorker = new CountDownLatch(1);
        CountDownLatch queuedTasksCompleted = new CountDownLatch(100);

        try {
            executor.execute(() -> {
                workerStarted.countDown();
                try {
                    releaseWorker.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            assertThat(workerStarted.await(5, TimeUnit.SECONDS)).isTrue();
            for (int i = 0; i < 100; i++) {
                executor.execute(queuedTasksCompleted::countDown);
            }
            connector.start();

            try (Socket rejected = connect(port)) {
                assertThat(rejected.getInputStream().read()).isEqualTo(-1);
            }

            releaseWorker.countDown();
            assertThat(queuedTasksCompleted.await(5, TimeUnit.SECONDS)).isTrue();
            try (Socket accepted = connect(port)) {
                accepted.getOutputStream().write(
                        "GET /unknown.html HTTP/1.1\r\nHost: localhost\r\n\r\n"
                                .getBytes(StandardCharsets.US_ASCII));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(accepted.getInputStream(), StandardCharsets.UTF_8));
                assertThat(reader.readLine()).isEqualTo("HTTP/1.1 404 Not Found");
            }
        } finally {
            releaseWorker.countDown();
            connector.stop();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    @DisplayName("스레드 풀이 종료되어 요청을 제출할 수 없으면 연결을 닫는다.")
    void closeConnectionWhenExecutorIsShutdown() throws Exception {
        int port = findAvailablePort();
        Connector connector = new Connector(port, 100, 1);
        executorService(connector).shutdown();
        connector.start();

        try (Socket connection = connect(port)) {
            assertThat(connection.getInputStream().read()).isEqualTo(-1);
        } finally {
            connector.stop();
        }
    }

    private Socket connect(int port) throws IOException {
        Socket socket = new Socket("localhost", port);
        socket.setSoTimeout(2000);
        return socket;
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
