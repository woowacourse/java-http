package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
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
    @DisplayName("켜져있는 서버 메인 페이지를 600개 요청후 성공이 스레드 개수 250과 큐 100개 합친 350인지 확인한다.")
    void request600() throws InterruptedException {
        // given
        int threadCount = 600;
        int expectedCount = 350;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            futures.add(pool.submit(() -> {
                HttpResponse<String> response = TestHttpUtils.send("/index.html");
                return response.statusCode();
            }));
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.MINUTES);

        long successCount = futures.stream()
                .filter(future -> {
                    try {
                        return future.get() == 200;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        // then
        assertThat(successCount).isEqualTo(expectedCount);
    }
}
