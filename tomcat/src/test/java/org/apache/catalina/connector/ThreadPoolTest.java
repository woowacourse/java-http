package org.apache.catalina.connector;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThreadPoolTest {

    @Test
    void 스레드_250개가_사용중이면_100개까지만_대기하고_초과_작업은_거부한다() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                250, 250, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.AbortPolicy());

        CountDownLatch started = new CountDownLatch(250);
        CountDownLatch release = new CountDownLatch(1);

        Runnable task = () -> {
            started.countDown();
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        try {
            // 250개 작업이 모두 실행될 때까지 기다린다.
            for (int i = 0; i < 250; i++) {
                executor.execute(task);
            }
            assertThat(started.await(10, TimeUnit.SECONDS)).isTrue();

            // 모든 작업 스레드가 사용 중이므로 추가 작업은 큐에 쌓인다.
            for (int i = 0; i < 100; i++) {
                executor.execute(task);
            }

            assertThat(executor.getPoolSize()).isEqualTo(250);
            assertThat(executor.getActiveCount()).isEqualTo(250);
            assertThat(executor.getQueue()).hasSize(100);

            assertThatThrownBy(() -> executor.execute(task))
                    .isInstanceOf(RejectedExecutionException.class);
            assertThat(executor.getQueue()).hasSize(100);
        } finally {
            release.countDown();
            executor.shutdownNow();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

}
