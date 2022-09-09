package org.apache.catalina.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TestHttpUtils {

    private static final Logger log = LoggerFactory.getLogger(TestHttpUtils.class);

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(1))
            .build();

    public static HttpResponse<String> send(final String path) {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .timeout(Duration.ofSeconds(1))
                .build();

        HttpResponse.BodyHandler<String> responseBodyHandler = null;
        try {
            responseBodyHandler = HttpResponse.BodyHandlers.ofString();
            final HttpResponse<String> send = httpClient.send(request, responseBodyHandler);
            return send;
        } catch (InterruptedException e) {
            System.err.println(responseBodyHandler);
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
