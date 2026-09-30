package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Test
    void waitsUpToAcceptCountWhenAllThreadsAreBusy() throws InterruptedException {
        final int maxThreads = 1;
        final int acceptCount = 100;
        final var executorService = Connector.createExecutorService(maxThreads, acceptCount);
        final var releaseWorker = new CountDownLatch(1);

        try {
            executorService.execute(() -> await(releaseWorker));

            for (int count = 0; count < acceptCount; count++) {
                executorService.execute(() -> {
                });
            }

            assertThat(executorService.getQueue()).hasSize(acceptCount);
            assertThatThrownBy(() -> executorService.execute(() -> {
                    }))
                    .isInstanceOf(RejectedExecutionException.class);
        } finally {
            releaseWorker.countDown();
            executorService.shutdownNow();
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    private void await(final CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
