package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    void createRequest() {
        String httpRequest = "GET /index.html HTTP/1.1 \n"
                + "Host: localhost:8080 \n"
                + "Connection: keep-alive ";

        Request request = null;
        try (InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            request = new Request(bufferedReader);
        } catch (IOException e) {
        }

        Request createdRequest = request;
        assertAll(
                () -> assertThat(createdRequest.getMethod()).isEqualTo("GET"),
                () -> assertThat(createdRequest.getRequestUrl()).isEqualTo("/index.html"),
                () -> assertThat(createdRequest.getVersion()).isEqualTo("HTTP/1.1")
        );
    }
}
