package org.apache;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class TestHttpUtils {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    public static HttpResponse<String> sendRegister(String account, String password, String email) {
        String body = String.format("account=%s&password=%s&email=%s", account, password, email);

        final var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/register"))
                .POST(BodyPublishers.ofString(body))
                .build();

        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
