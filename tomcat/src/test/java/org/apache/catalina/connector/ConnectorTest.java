package org.apache.catalina.connector;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.RequestMapping;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    @Test
    void maxThreads만큼의_스레드로_요청을_처리하고_초과_요청은_대기한다() throws Exception {
        // given
        final int maxThreads = 2;
        final var started = new CountDownLatch(maxThreads);
        final var release = new CountDownLatch(1);
        final var completed = new CountDownLatch(3);
        final var activeCount = new AtomicInteger();
        final var maxActiveCount = new AtomicInteger();

        final Controller controller = (request, response) -> {
            final int currentActiveCount = activeCount.incrementAndGet();
            maxActiveCount.accumulateAndGet(currentActiveCount, Math::max);
            started.countDown();

            try {
                release.await();
                response.setBody("OK".getBytes(StandardCharsets.UTF_8));
            } finally {
                activeCount.decrementAndGet();
                completed.countDown();
            }
        };

        final var requestMapping = new RequestMapping(Map.of("/slow", controller));
        final var connector = new Connector(availablePort(), 100, maxThreads, requestMapping);
        final ThreadPoolExecutor executor = executorOf(connector);

        try {
            // when
            process(connector, new StubSocket(request()));
            process(connector, new StubSocket(request()));
            process(connector, new StubSocket(request()));

            // then
            assertThat(started.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(executor.getPoolSize()).isEqualTo(maxThreads);
            assertThat(executor.getActiveCount()).isEqualTo(maxThreads);
            assertThat(executor.getQueue()).hasSize(1);

            release.countDown();

            assertThat(completed.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(maxActiveCount).hasValue(maxThreads);
        } finally {
            release.countDown();
            connector.stop();
        }

        assertThat(executor.isShutdown()).isTrue();
    }

    private int availablePort() throws Exception {
        try (final var serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }

    private String request() {
        return String.join("\r\n",
                "GET /slow HTTP/1.1",
                "Host: localhost",
                "",
                "");
    }

    private void process(final Connector connector, final StubSocket socket) throws Exception {
        final Method process = Connector.class.getDeclaredMethod("process", java.net.Socket.class);
        process.setAccessible(true);
        process.invoke(connector, socket);
    }

    private ThreadPoolExecutor executorOf(final Connector connector) throws Exception {
        final Field executorService = Connector.class.getDeclaredField("executorService");
        executorService.setAccessible(true);
        return (ThreadPoolExecutor) executorService.get(connector);
    }
}
