package org.apache.catalina.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

class ConnectorTest {

    @Test
    @DisplayName("켜져있는 서버 메인 페이지를 600개 요청 해본다.")
    void request600() throws InterruptedException {
        // given
        int threadCount = 600;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            futures.add(pool.submit(() -> TestHttpUtils.send("/index.html")));
        }
        pool.awaitTermination(10, TimeUnit.SECONDS);

        long doneCount = futures.stream()
                .filter(Future::isDone)
                .count();

        // then
        System.out.println("doneCount = " + doneCount);
    }
}
