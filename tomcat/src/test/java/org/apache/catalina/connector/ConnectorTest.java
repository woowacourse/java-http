package org.apache.catalina.connector;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConnectorTest {

    private static final int PORT = 8080;

    private Connector connector;

    @BeforeEach
    void setUp() {
        connector = new Connector();
        connector.start();
    }

    @AfterEach
    void tearDown() {
        connector.stop();
    }

    @Test
    void testConnectorHandles301Requests() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(301);

        AtomicInteger successfulConnections = new AtomicInteger();

        CountDownLatch latch = new CountDownLatch(301);

        for (int i = 0; i < 400; i++) {
            executorService.submit(() -> {
                try {
                    HttpClient httpClient = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(20))
                            .build();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:" + PORT + "/"))
                            .timeout(Duration.ofSeconds(10))
                            .GET()
                            .build();

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        successfulConnections.incrementAndGet();
                    }
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        assertThat(successfulConnections.get())
                .isLessThan(350);
        assertThat(successfulConnections.get())
                .isGreaterThan(200);
    }
}
