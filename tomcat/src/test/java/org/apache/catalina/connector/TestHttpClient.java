package org.apache.catalina.connector;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class TestHttpClient {

    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger failCount = new AtomicInteger(0);

    private final HttpClient httpClient;

    public TestHttpClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(1))
                .build();
    }

    public HttpResponse<String> send(final String path) {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081" + path))
                .timeout(Duration.ofSeconds(1))
                .build();

        try {
            HttpResponse<String> send = httpClient.send(request, BodyHandlers.ofString());
            successCount.incrementAndGet();
            return send;
        } catch (IOException | InterruptedException e) {
            failCount.incrementAndGet();
            throw new RuntimeException(e);
        }
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public int getFailCount() {
        return failCount.get();
    }
}
