package support;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TestHttpUtils {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static HttpResponse<String> sendGet(final String host, final String path) {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(host + path))
                .timeout(Duration.ofSeconds(10))
                .build();

        return send(request);

    }

    public static HttpResponse<String> sendPost(final String host, final String path, final String body) {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(host + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return send(request);
    }

    private static HttpResponse<String> send(final HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
