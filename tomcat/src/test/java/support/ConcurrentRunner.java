package support;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * 여러 스레드가 같은 순간에 작업을 시작하도록 맞춘 뒤 실행한다.
 * 모든 스레드가 준비될 때까지 기다렸다가 한꺼번에 출발시켜 경합이 최대한 일어나게 한다.
 */
public final class ConcurrentRunner {
    private static final long TIMEOUT_SECONDS = 10;

    private ConcurrentRunner() {
    }

    public static void run(final int threadCount, final IntConsumer task) throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch ready = new CountDownLatch(threadCount);
        final CountDownLatch start = new CountDownLatch(1);

        try {
            final List<Future<Object>> futures = IntStream.range(0, threadCount)
                    .mapToObj(index -> executor.submit(() -> {
                        ready.countDown();
                        start.await();
                        task.accept(index);
                        return null;
                    }))
                    .toList();

            ready.await();
            start.countDown();

            for (final Future<Object> future : futures) {
                future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
