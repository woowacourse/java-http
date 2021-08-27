package nextstep.learning.http;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadTest {

    @Test
    void testCounterWithConcurrency() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(10);

        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        MyCounter counter = new MyCounter();

        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                counter.increment();
                latch.countDown();
            });
        }
        System.out.println("latch.getCount() = " + latch.getCount());
        latch.await();
        System.out.println("latch.getCount() = " + latch.getCount());
        assertThat(counter.getCount()).isEqualTo(numberOfThreads);
    }

    @Test
    void testSummationWithConcurrency() throws InterruptedException {
        int numberOfThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(1);

        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        MyCounter counter = new MyCounter();

        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                counter.increment();
                latch.countDown();
            });
        }
        latch.await();
        assertThat(counter.getCount()).isEqualTo(numberOfThreads);
    }

    @Test
    void executorSubmit() throws InterruptedException {
        final ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Job1 = " + threadName);
        });
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Job2 = " + threadName);
        });
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Job3 = " + threadName);
        });
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Job4 = " + threadName);
            return 1;
        });

        // 더 이상 ExecutorService에 Task 추가 X
        // 작업 완료시 쓰레드풀 종료
        executor.shutdown();

        if (executor.awaitTermination(20, TimeUnit.SECONDS)) {
            System.out.println(LocalTime.now() + "All jobs done!");
        } else {
            System.out.println(LocalTime.now() + "Some jobs left!");
            executor.shutdownNow();
        }
    }

    @Test
    void threadTask() {
        Runnable task = new Runnable() {
            public void run() {
                System.out.println("Thread = " + Thread.currentThread().getName());
            }
        };
        for (int i=0; i<10; i++) {
            final Thread thread = new Thread(task);
            thread.start();
        }

        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i=0; i<10; i++) {
            service.submit(task);
        }

    }

    static class MyCounter {

        private int count;

        public void increment() {
            int temp = count;
            count = temp + 1;
        }

        public int getCount() {
            return count;
        }
    }
}
