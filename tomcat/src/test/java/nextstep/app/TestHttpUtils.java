package nextstep.app;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import nextstep.jwp.dto.UserRegisterRequest;

public class TestHttpUtils {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(1))
            .build();

    public static HttpResponse<String> send(final String path) {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .build();

        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse<String> postRegister(final UserRegisterRequest userRegisterRequest) {
        final String body = String.join("&",
                "account=" + userRegisterRequest.getAccount(),
                "email=" + userRegisterRequest.getEmail(),
                "password=" + userRegisterRequest.getPassword());
        final var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + "/register"))
                .POST(BodyPublishers.ofString(body))
                .build();

        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
