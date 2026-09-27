package support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

public final class ConcurrentTestSupport {

    private ConcurrentTestSupport() {
    }

    public static void runConcurrently(
            final int workerCount,
            final IntConsumer action
    ) throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        final CountDownLatch ready = new CountDownLatch(workerCount);
        final CountDownLatch start = new CountDownLatch(1);
        final List<Future<?>> tasks = new ArrayList<>();

        try {
            for (int i = 0; i < workerCount; i++) {
                final int workerIndex = i;
                tasks.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("동시 작업 시작 시간이 초과되었습니다.");
                    }
                    action.accept(workerIndex);
                    return null;
                }));
            }

            if (!ready.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("작업 스레드 준비 시간이 초과되었습니다.");
            }
            start.countDown();

            for (final Future<?> task : tasks) {
                task.get(5, TimeUnit.SECONDS);
            }
        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }
}
