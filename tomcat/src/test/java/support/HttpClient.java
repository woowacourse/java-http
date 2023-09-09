package support;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.apache.coyote.http.request.ContentType;

public class HttpClient {

    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                                                                                   .version(
                                                                                       Version.HTTP_1_1)
                                                                                   .build();

    public static HttpResponse<String> send(int port, final String path, final int timeout) {
        var request = java.net.http.HttpRequest.newBuilder()
                                               .uri(URI.create("http://localhost:" + port + path))
                                               .timeout(Duration.of(timeout, ChronoUnit.MILLIS))
                                               .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse<String> send(final int port, final String path) {
        var request = java.net.http.HttpRequest.newBuilder()
                                               .uri(URI.create("http://localhost:" + port + path))
                                               .GET()
                                               .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse<String> send(final int port, final String path, Map<String, String> headers) {
        var builder = HttpRequest.newBuilder()
                                 .uri(URI.create("http://localhost:" + port + path))
                                 .GET();

        headers.forEach(builder::header);
        var request = builder.build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse<String> send(final int port, final String path, final String body,
        final ContentType contentType) {
        var request = java.net.http.HttpRequest.newBuilder()
                                               .uri(URI.create("http://localhost:" + port + path))
                                               .header("Content-Type", contentType.getValue())
                                               .POST(HttpRequest.BodyPublishers.ofString(body,
                                                   StandardCharsets.UTF_8))
                                               .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
