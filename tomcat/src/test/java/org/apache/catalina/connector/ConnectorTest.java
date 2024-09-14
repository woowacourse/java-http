package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

class ConnectorTest {

    private static final AtomicInteger count = new AtomicInteger(0);

    @DisplayName("동시 요청을 보내본다.")
    @Test
    void start() throws Exception {

        int threadsCounts = 100;
        try (ExecutorService executorService = Executors.newFixedThreadPool(threadsCounts)) {
            IntStream.range(0, threadsCounts)
                    .forEach(i -> {
                        executorService.submit(() -> incrementIfOk(TestHttpUtils.send("/index.html")));
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });

            executorService.shutdown();
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }

            assertThat(count.intValue()).isEqualTo(threadsCounts);
        }
    }

    private static void incrementIfOk(final HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            count.incrementAndGet();
        }
    }
}
