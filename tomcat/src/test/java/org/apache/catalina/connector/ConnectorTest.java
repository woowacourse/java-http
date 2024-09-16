package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.servlet.DispatcherServlet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    private static final int DEFAULT_PORT = 8081;

    @Test
    void concurrencyRequestTest() throws InterruptedException {
        final int DEFAULT_ACCEPT_COUNT = 100;
        final int MAX_THREAD_POOL_COUNT = 10;
        final int CLIENT_REQUEST_COUNT = 100;

        Connector connector = new Connector(
                DEFAULT_PORT,
                DEFAULT_ACCEPT_COUNT,
                MAX_THREAD_POOL_COUNT,
                new DispatcherServlet()
        );
        connector.start();

        TestHttpClient testHttpClient = new TestHttpClient();
        ExecutorService clientExecutor = Executors.newFixedThreadPool(CLIENT_REQUEST_COUNT);
        CountDownLatch requestLatch = new CountDownLatch(CLIENT_REQUEST_COUNT);
        for (int i = 0; i < CLIENT_REQUEST_COUNT; i++) {
            clientExecutor.submit(() -> {
                try {
                    testHttpClient.send("/");
                } finally {
                    requestLatch.countDown();
                }
            });
        }
        requestLatch.await(2, TimeUnit.SECONDS);
        clientExecutor.shutdown();
        connector.stop();

        assertAll(
                () -> assertThat(testHttpClient.getSuccessCount()).isEqualTo(CLIENT_REQUEST_COUNT),
                () -> assertThat(testHttpClient.getFailCount()).isEqualTo(0)
        );
    }

    @Disabled
    @DisplayName("최대 ThradPool의 크기는 250, 동시 요청이 400, 대기 요청 가능 수 100이면 350건을 처리 가능하고, 50건은 실패한다.")
    @Test
    void threadPoolCapacityAndWaitingQueueTest() throws Exception {
        final int THREAD_POOL_SIZE = 250;
        final int QUEUE_CAPACITY = 100;
        final int TOTAL_REQUESTS = 400;

        Connector connector = new Connector(
                DEFAULT_PORT,
                QUEUE_CAPACITY,
                THREAD_POOL_SIZE,
                new DispatcherServlet()
        );
        connector.start();

        TestHttpClient testHttpClient = new TestHttpClient();
        ExecutorService clientExecutor = Executors.newFixedThreadPool(TOTAL_REQUESTS);
        CountDownLatch requestLatch = new CountDownLatch(TOTAL_REQUESTS);
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            clientExecutor.submit(() -> {
                try {
                    testHttpClient.send("/");
                } finally {
                    requestLatch.countDown();
                }
            });
        }
        requestLatch.await(2, TimeUnit.SECONDS);
        clientExecutor.shutdown();
        connector.stop();

        assertAll(
                () -> assertThat(testHttpClient.getSuccessCount()).isEqualTo(THREAD_POOL_SIZE + QUEUE_CAPACITY),
                () -> assertThat(testHttpClient.getFailCount()).isEqualTo(
                        TOTAL_REQUESTS - (THREAD_POOL_SIZE + QUEUE_CAPACITY))
        );
    }
}
