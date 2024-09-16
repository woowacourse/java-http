package org.apache.catalina.connector;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    private static final int ACCEPT_COUNT = 100;
    private static final int MAX_THREADS = 250;

    private Connector connector;

    @BeforeEach
    void setUp() {
        connector = new Connector(8080, ACCEPT_COUNT, MAX_THREADS);
    }

    @AfterEach
    void tearDown() {
        connector.stop();
    }

    @Test
    void testThreadPoolDefaultSize() {
        ThreadPoolExecutor threadPool = ((ThreadPoolExecutor) connector.getThreadPool());

        for (int i = 0; i < 10; i++) {
            threadPool.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        assertAll(
                () -> assertEquals(10, threadPool.getPoolSize()),
                () -> assertEquals(0, threadPool.getQueue().size())
        );
    }

    @Test
    void testThreadPoolSize() {
        ThreadPoolExecutor threadPool = ((ThreadPoolExecutor) connector.getThreadPool());

        for (int i = 0; i < 300; i++) {
            threadPool.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        assertAll(
                () -> assertEquals(MAX_THREADS, threadPool.getPoolSize()),
                () -> assertEquals(300 - MAX_THREADS, threadPool.getQueue().size())
        );
    }

    @Test
    void testThreadPoolMaxSize() {
        ThreadPoolExecutor threadPool = ((ThreadPoolExecutor) connector.getThreadPool());

        try {
            for (int i = 0; i < 1000; i++) {
                threadPool.submit(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (RejectedExecutionException e) {
        }

        assertAll(
                () -> assertEquals(MAX_THREADS, threadPool.getPoolSize()),
                () -> assertEquals(ACCEPT_COUNT, threadPool.getQueue().size())
        );
    }
}
