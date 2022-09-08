package org.apache.coyote.support;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class AATest {

    @Test
    void test() throws InterruptedException {
        int numTasks = 60;
        CountDownLatch countDownLatch = new CountDownLatch(numTasks);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 50, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());

        for (int i = 0; i < numTasks; i++) {
            threadPoolExecutor.submit(() -> {
                //Do something
                countDownLatch.countDown();
            });
        }
    }

    public static class Worker implements Runnable {
        private CountDownLatch countDownLatch;

        public Worker(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            System.out.println("Do something (tid: " + Thread.currentThread().getId() + ")");
            countDownLatch.countDown();
            System.out.printf("Latch's Count : (%d)\r\n", countDownLatch.getCount());
        }
    }

    @Test
    void test2() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        List<Thread> workers = Stream
                .generate(() -> new Thread(new Worker(countDownLatch)))
                .limit(5)
                .collect(Collectors.toList());

        System.out.println("Start multi threads (tid: " + Thread.currentThread().getId() + ")");

        workers.forEach(Thread::start);

        System.out.println("Waiting for some work to be finished (tid: " + Thread.currentThread().getId() + ")");

        countDownLatch.await();

        System.out.println("Finished (tid: " + Thread.currentThread().getId() + ")");
    }
}
