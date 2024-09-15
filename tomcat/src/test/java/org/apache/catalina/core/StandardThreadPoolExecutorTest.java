package org.apache.catalina.core;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StandardThreadPoolExecutorTest {

    @Test
    public void testMaxThreadAndQueueCapacity() {
        int maxThreads = 250;
        int acceptCount = 100;
        try (StandardThreadPoolExecutor executor = new StandardThreadPoolExecutor(maxThreads, acceptCount)) {
            for (int i = 0; i < maxThreads; i++) {
                executor.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            for (int i = 0; i < acceptCount; i++) {
                executor.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            assertAll(
                    () -> assertThat(executor.getActiveCount()).isEqualTo(maxThreads),
                    () -> assertThat(executor.getQueue().size()).isEqualTo(acceptCount),
                    () -> assertThrows(RejectedExecutionException.class, () -> {
                        executor.submit(() -> System.out.println("This task should be rejected"));
                    })
            );
        }
    }
}
