package org.apache.coyote.request;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class RequestTest {

    @Test
    void 요청이_각_클래스에_맞춰_제대로_파싱되었는지_확인한다() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Connection: keep-alive ",
                "Content-Length: 24 ",
                "",
                "account=kero&name=keroro");

        final var connection = new StubSocket(httpRequest);
        final List<String> requestLines = Arrays.stream(httpRequest.split(System.lineSeparator()))
                .collect(Collectors.toList());

        // when
        try (connection;
             final var inputStream = connection.getInputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final Request request = Request.from(requestLines, bufferedReader);

            // then
            assertThat(request).hasToString(httpRequest);
        }
    }

    @Test
    void requestBody가_없을때_파싱_확인() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var connection = new StubSocket(httpRequest);
        final List<String> requestLines = Arrays.stream(httpRequest.split(System.lineSeparator()))
                .collect(Collectors.toList());

        // when
        try (connection;
             final var inputStream = connection.getInputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final Request request = Request.from(requestLines, bufferedReader);

            // then
            assertThat(request).hasToString(httpRequest);
        }

    }
}
