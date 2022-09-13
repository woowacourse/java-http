package org.apache.catalina.executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Executor는")
class ExecutorTest {

    private AtomicInteger counter;

    @BeforeEach
    void setUp() {
        counter = new AtomicInteger(0);
    }

    @DisplayName("기본적으로 corePoolSize 개수만큼의 요청만 동시에 처리하며, 그 이상의 요청들은 큐에 대기한다.")
    @Test
    void corePoolSize() throws InterruptedException {
        final int corePoolSize = 5;
        final var executor = new Executor(5, 10, 100, 10);

        for (int i = 0; i < 30; i++) {
            executor.execute(incrementAndSleepForever(counter));
        }

        Thread.sleep(100);
        assertThat(counter.get()).isEqualTo(corePoolSize);
        assertThatNoException().isThrownBy(() -> executor.execute(counter::incrementAndGet));
    }

    @DisplayName("대기 중인 요청의 개수가 큐의 크기를 초과하는 경우, 쓰레드의 개수를 증가시켜 동시에 처리하는 작업의 개수를 늘린다.")
    @Test
    void poolSize_increaseOnFullQueue() throws InterruptedException {
        final int corePoolSize = 10;
        final int maxQueueSize = 20;
        final var executor = new Executor(corePoolSize, 100, maxQueueSize, 10);

        for (int i = 0; i < corePoolSize + maxQueueSize; i++) {
            executor.execute(incrementAndSleepForever(counter));
        }
        assertThat(counter.get()).isEqualTo(corePoolSize);

        final int overwork = 15;
        for (int i = 0; i < overwork; i++) {
            executor.execute(incrementAndSleepForever(counter));
        }
        Thread.sleep(100);
        assertThat(counter.get()).isEqualTo(corePoolSize + overwork);
    }

    @DisplayName("corePoolSize 값보다 더 많이 생성된 쓰레드들은 keepAliveTime 기간 동안 작업이 들어오지 않으면 corePoolSize까지 순차적으로 제거된다.")
    @Test
    void keepAliveTime() throws InterruptedException {
        final int corePoolSize = 10;
        final int maxThreads = 25;
        final int maxQueueSize = 10;
        final int keepAliveTime = 10;
        final var executor = new Executor(corePoolSize, maxThreads, maxQueueSize, keepAliveTime);
        final int jobExecutionTime = 1000;
        for (int i = 0; i < maxThreads + maxQueueSize; i++) {
            executor.execute(incrementAndSleep(counter, jobExecutionTime));
        }
        Thread.sleep(500);
        assertThat(counter.get()).isEqualTo(maxThreads);

        Thread.sleep(jobExecutionTime + keepAliveTime);
        counter = new AtomicInteger(0);
        for (int i = 0; i < corePoolSize + maxQueueSize; i++) {
            executor.execute(incrementAndSleepForever(counter));
        }
        assertThat(counter.get()).isLessThan(maxThreads);
    }

    @DisplayName("maxThreads 개수만큼의 쓰레드만 생성될 수 있으며, 큐가 꽉 찬 상태에서 요청이 들어오면 예외가 발생한다.")
    @Test
    void exceptionOnFullQueue() throws InterruptedException {
        final int maxThreads = 10;
        final int maxQueueSize = 20;
        final int maxConcurrencyCapacity = maxThreads + maxQueueSize;
        final var executor = new Executor(5, maxThreads, maxQueueSize, 10);

        for (int i = 0; i < maxConcurrencyCapacity; i++) {
            executor.execute(incrementAndSleepForever(counter));
        }

        Thread.sleep(100);
        assertThat(counter.get()).isEqualTo(maxThreads);
        assertThatThrownBy(() -> executor.execute(counter::incrementAndGet))
                .isInstanceOf(RejectedExecutionException.class);
    }

    private Runnable incrementAndSleepForever(AtomicInteger counter) {
        return incrementAndSleep(counter, 100000000);
    }

    private Runnable incrementAndSleep(AtomicInteger counter, int sleepTime) {
        return () -> {
            counter.incrementAndGet();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {
            }
        };
    }
}
