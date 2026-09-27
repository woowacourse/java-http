package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.coyote.http11.HandlerMapping;
import org.apache.coyote.http11.RequestDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
