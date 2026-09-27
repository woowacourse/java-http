package org.apache.catalina.connector;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.coyote.Adapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import support.StubSocket;

class ConnectorTest {

    private static final int MAX_THREADS = 2;

    private final CountDownLatch runningRequestsStarted = new CountDownLatch(MAX_THREADS);
    private final CountDownLatch acceptQueuedRequest = new CountDownLatch(1);
    private final CountDownLatch requestsSubmitted = new CountDownLatch(1);
    private final CountDownLatch queuedRequestStarted = new CountDownLatch(1);
    private final CountDownLatch queuedResponseCompleted = new CountDownLatch(1);
    private final CountDownLatch listenerClosed = new CountDownLatch(1);
    private final Semaphore finishRunningRequest = new Semaphore(0);

    private MockedConstruction<ServerSocket> serverSockets;
    private Connector connector;
    private Thread connectorThread;
    private ThreadPoolExecutor executor;
    private StubSocket queuedSocket;
    private List<StubSocket> excessRequests = List.of();

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        queuedSocket = createQueuedSocket();
        final var acceptedRequests = new AtomicInteger();
        serverSockets = mockConstruction(ServerSocket.class, (serverSocket, context) -> {
            when(serverSocket.accept()).thenAnswer(invocation -> {
                final int requestIndex = acceptedRequests.getAndIncrement();
                if (requestIndex < MAX_THREADS) {
                    return new StubSocket();
                }
                if (requestIndex == MAX_THREADS) {
                    acceptQueuedRequest.await();
                    return queuedSocket;
                }

                final int excessRequestIndex = requestIndex - MAX_THREADS - 1;
                if (excessRequestIndex < excessRequests.size()) {
                    return excessRequests.get(excessRequestIndex);
                }

                // The next accept starts only after the previous request was submitted.
                requestsSubmitted.countDown();
                listenerClosed.await();
                return null;
            });
            doAnswer(invocation -> {
                acceptQueuedRequest.countDown();
                listenerClosed.countDown();
                return null;
            }).when(serverSocket).close();
        });

        connector = new Connector(8080, 1, MAX_THREADS, blockingAdapter());
        final var executorField = Connector.class.getDeclaredField("executorService");
        executorField.setAccessible(true);
        executor = (ThreadPoolExecutor) executorField.get(connector);
        connectorThread = new Thread(connector);
        connectorThread.setDaemon(true);
    }

    @Test
    @DisplayName("최대 스레드 수를 초과한 요청은 대기열에서 기다린다")
    void queuesRequestWhenMaxThreadsAreBusy() throws InterruptedException {
        // given
        connectorThread.start();
        assertThat(runningRequestsStarted.await(5, SECONDS)).isTrue();

        // when
        acceptQueuedRequest.countDown();
        assertThat(requestsSubmitted.await(5, SECONDS)).isTrue();

        // then
        assertThat(executor.getActiveCount()).isEqualTo(MAX_THREADS);
        assertThat(executor.getQueue()).hasSize(1);
        assertThat(queuedRequestStarted.getCount()).isEqualTo(1);
        assertThat(queuedResponseCompleted.getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("실행 중인 작업이 끝나면 대기 중인 요청을 처리한다")
    void processesQueuedRequestWhenRunningTaskCompletes() throws InterruptedException {
        // given
        connectorThread.start();
        assertThat(runningRequestsStarted.await(5, SECONDS)).isTrue();
        acceptQueuedRequest.countDown();
        assertThat(requestsSubmitted.await(5, SECONDS)).isTrue();
        assertThat(executor.getQueue()).hasSize(1);
        assertThat(queuedRequestStarted.getCount()).isEqualTo(1);

        // when
        finishRunningRequest.release();

        // then
        assertThat(queuedResponseCompleted.await(5, SECONDS)).isTrue();
        assertThat(queuedRequestStarted.getCount()).isZero();
        assertThat(executor.getQueue()).isEmpty();
        assertThat(queuedSocket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .endsWith("\r\n\r\nqueued request completed");
    }

    @Test
    @DisplayName("스레드와 대기열이 모두 차면 초과 요청의 연결을 닫고 기존 요청은 계속 처리한다")
    void closesExcessConnectionsWhenThreadsAndQueueAreFull() throws InterruptedException {
        // given
        excessRequests = List.of(new StubSocket(), new StubSocket());
        connectorThread.start();
        assertThat(runningRequestsStarted.await(5, SECONDS)).isTrue();

        // when
        acceptQueuedRequest.countDown();
        assertThat(requestsSubmitted.await(5, SECONDS)).isTrue();

        // then
        assertThat(executor.getActiveCount()).isEqualTo(MAX_THREADS);
        assertThat(executor.getQueue()).hasSize(1);
        assertThat(queuedRequestStarted.getCount()).isEqualTo(1);
        for (StubSocket socket : excessRequests) {
            assertThat(socket.isClosed()).isTrue();
            assertThat(socket.output()).isEmpty();
        }
        assertThat(connectorThread.isAlive()).isTrue();

        finishRunningRequest.release();
        assertThat(queuedResponseCompleted.await(5, SECONDS)).isTrue();
        assertThat(executor.getQueue()).isEmpty();
        assertThat(queuedSocket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .endsWith("\r\n\r\nqueued request completed");
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        try {
            if (connector != null) {
                connector.stop();
            }
            finishRunningRequest.release(MAX_THREADS);
            if (executor != null) {
                executor.shutdown();
                assertThat(executor.awaitTermination(5, SECONDS)).isTrue();
            }
            if (connectorThread != null) {
                connectorThread.join(5000);
                assertThat(connectorThread.isAlive()).isFalse();
            }
        } finally {
            if (executor != null) {
                executor.shutdownNow();
            }
            if (serverSockets != null) {
                serverSockets.close();
            }
        }
    }

    private Adapter blockingAdapter() {
        return (request, response) -> {
            if (request.getRequestLine().getPath().equals("/queued")) {
                queuedRequestStarted.countDown();
                response.setBody("queued request completed");
                return;
            }

            runningRequestsStarted.countDown();
            try {
                finishRunningRequest.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException(e);
            }
            response.setBody("running request completed");
        };
    }

    private StubSocket createQueuedSocket() {
        return new StubSocket("GET /queued HTTP/1.1\r\nHost: localhost\r\n\r\n") {
            @Override
            public OutputStream getOutputStream() {
                return new FilterOutputStream(super.getOutputStream()) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        queuedResponseCompleted.countDown();
                    }
                };
            }
        };
    }
}
