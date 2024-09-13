package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    @Nested
    class 스레드_풀 {

        private static final AtomicInteger count = new AtomicInteger(0);

        @Test
        void 스레드_풀을_생성한다() throws Exception {
            final var NUMBER_OF_THREAD = 500;
            var threads = new Thread[NUMBER_OF_THREAD];

            for (int i = 0; i < NUMBER_OF_THREAD; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        incrementIfOk(TestHttpUtils.send("/register"));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            for (final var thread : threads) {
                thread.start();
            }

            for (final var thread : threads) {
                thread.join();
            }
            assertThat(count.intValue()).isEqualTo(250);
        }

        private static void incrementIfOk(final HttpResponse<String> response) {
            if (response.statusCode() == 200) {
                count.incrementAndGet();
            }
        }
    }
}


class TestHttpUtils {

    public static final HttpClient httpClient = HttpClient.newBuilder()
            .version(Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(1))
            .build();

    public static HttpResponse<String> send(String path) throws InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .timeout(Duration.ofSeconds(1))
                .build();

        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
