package org.apache.coyote.request;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class HttpHttpRequestTest {
    @Test
    void requestBody가_없을때_파싱_확인() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");

        final var connection = new StubSocket(httpRequest);

        // when
        try (connection;
             final var inputStream = connection.getInputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest request = HttpRequest.from(RequestReader.from(bufferedReader));

            // then
            assertThat(request.toString()).contains(httpRequest);
        }
    }
}
