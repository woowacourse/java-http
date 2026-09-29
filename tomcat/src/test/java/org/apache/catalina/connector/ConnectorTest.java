package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.HandlerMapping;
import org.apache.catalina.RequestDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.BlockingSocket;

class ConnectorTest {

    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() {
        this.requestDispatcher = new RequestDispatcher(
            new HandlerMapping("com.techcourse.controller"));
    }

    @Test
    void 설정한_스레드_수로_고정_스레드_풀을_생성한다() throws Exception {
        final int maxThreads = 3;
        final Connector connector = new Connector(requestDispatcher, availablePort(), 100,
            maxThreads);

        try {
            final ThreadPoolExecutor executor = threadPoolExecutorOf(connector);

            assertThat(executor.getCorePoolSize()).isEqualTo(maxThreads);
            assertThat(executor.getMaximumPoolSize()).isEqualTo(maxThreads);
        } finally {
            connector.stop();
        }
    }

    @Test
    void 커넥터를_중지하면_스레드_풀도_종료한다() throws Exception {
        final Connector connector = new Connector(requestDispatcher, availablePort(), 100, 1);
        final ExecutorService executorService = executorServiceOf(connector);

        connector.stop();

        assertThat(executorService.isShutdown()).isTrue();
    }

    @Test
    void 스레드와_대기열이_가득_차면_추가_연결을_종료한다() throws Exception {
        final int maxThreads = 2;
        final int acceptCount = 1;
        final Connector connector = new Connector(
            requestDispatcher,
            availablePort(),
            acceptCount,
            maxThreads);
        final ThreadPoolExecutor executor = threadPoolExecutorOf(connector);
        final CountDownLatch workersStarted = new CountDownLatch(maxThreads);
        final CountDownLatch releaseWorkers = new CountDownLatch(1);
        final Socket first = new BlockingSocket(workersStarted, releaseWorkers);
        final Socket second = new BlockingSocket(workersStarted, releaseWorkers);
        final Socket queued = new BlockingSocket(workersStarted, releaseWorkers);
        final Socket rejected = mock(Socket.class);

        try {
            process(connector, first);
            process(connector, second);
            assertThat(workersStarted.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(executor.getActiveCount()).isEqualTo(maxThreads);

            process(connector, queued);
            assertThat(executor.getQueue()).hasSize(acceptCount);

            process(connector, rejected);

            verify(rejected).close();
            assertThat(executor.getQueue()).hasSize(acceptCount);
        } finally {
            releaseWorkers.countDown();
            connector.stop();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    private void process(final Connector connector, final Socket socket) throws Exception {
        final Method method = Connector.class.getDeclaredMethod("process", Socket.class);
        method.setAccessible(true);
        method.invoke(connector, socket);
    }

    private ThreadPoolExecutor threadPoolExecutorOf(final Connector connector)
        throws ReflectiveOperationException {
        return (ThreadPoolExecutor) executorServiceOf(connector);
    }

    private ExecutorService executorServiceOf(final Connector connector)
        throws ReflectiveOperationException {
        final Field field = Connector.class.getDeclaredField("executorService");
        field.setAccessible(true);
        return (ExecutorService) field.get(connector);
    }

    private int availablePort() throws IOException {
        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
