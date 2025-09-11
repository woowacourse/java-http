package org.apache.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class CustomTaskQueueTest {

    @Test
    void customTaskQueue는_maxThreads까지_스레드를_늘린다() throws InterruptedException {
        // given
        final int core = 1;
        final int max = 3;
        final int queueSize = 10;

        final var queue = new CustomTaskQueue(queueSize);
        final var executor = new ThreadPoolExecutor(
                core,
                max,
                60L, TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.AbortPolicy()
        );
        queue.setParent(executor);

        // when
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(500); // 일부러 오래 걸리게
                } catch (InterruptedException ignored) {
                }
            });
        }

        // then
        Thread.sleep(100); // 스레드가 늘어날 시간을 조금 줌

        System.out.println("Active count = " + executor.getActiveCount());
        System.out.println("Pool size   = " + executor.getPoolSize());
        System.out.println("Queue size  = " + executor.getQueue()
                .size());

        // CustomTaskQueue 덕분에, 큐에 넣기 전에 max까지 스레드를 늘림
        assertThat(executor.getPoolSize()).isEqualTo(max);
        assertThat(executor.getQueue()
                .size()).isEqualTo(2); // 나머지는 큐에
    }

    @Test
    void 기본_LinkedBlockingQueue는_큐부터_쌓는다() throws InterruptedException {
        // given
        final int core = 1;
        final int max = 3;
        final int queueSize = 10;

        final var queue = new LinkedBlockingQueue<Runnable>(queueSize);
        final var executor = new ThreadPoolExecutor(
                core,
                max,
                60L, TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.AbortPolicy()
        );

        // when
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            });
        }

        // then
        Thread.sleep(100);

        System.out.println("Active count = " + executor.getActiveCount());
        System.out.println("Pool size   = " + executor.getPoolSize());
        System.out.println("Queue size  = " + executor.getQueue()
                .size());

        // 기본 정책: core 이상은 큐에 먼저 쌓음
        assertThat(executor.getPoolSize()).isEqualTo(core);
        assertThat(executor.getQueue()
                .size()).isGreaterThan(0);
    }
}
