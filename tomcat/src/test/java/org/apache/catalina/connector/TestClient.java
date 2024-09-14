package org.apache.catalina.connector;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;

public class TestClient {

    private static final int QUEUED_THRESHOLD = 15000;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))  // Connection timeout
            .build();

    public static SimpleEntry<ResponseType, HttpResponse<String>> send(String path, int id) {
        final var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .header("id", String.valueOf(id))
                .timeout(Duration.ofSeconds(30))  // Read timeout
                .build();

        try {
            long start = System.currentTimeMillis();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            long elapsed = System.currentTimeMillis() - start;
            if (response.statusCode() == 200) {
                ResponseType responseType = ResponseType.of(response, elapsed);
                return new SimpleEntry<>(responseType, response);
            }
        } catch (HttpTimeoutException e) {  // read timeout
            return new SimpleEntry<>(ResponseType.FAILED, null);
        } catch (IOException | InterruptedException e) {
            if (isRequestRefused(e)) {
                return new SimpleEntry<>(ResponseType.FAILED, null);
            }
            throw new RuntimeException(e);
        }
        return null;
    }

    private static boolean isRequestRefused(Throwable cause) {
        Throwable rootCause = getRootCause(cause);
        return rootCause instanceof SocketException || rootCause instanceof EOFException;
    }

    private static Throwable getRootCause(Throwable e) {
        Throwable cause;
        Throwable result = e;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

    public enum ResponseType {
        SUCCESS, SUCCESS_QUEUED, FAILED;

        public static ResponseType of(HttpResponse<String> response, long elapsed) {
            if (response == null || response.statusCode() != 200) {
                return FAILED;
            }
            return elapsed >= QUEUED_THRESHOLD ? SUCCESS_QUEUED : SUCCESS;
        }
    }
}
