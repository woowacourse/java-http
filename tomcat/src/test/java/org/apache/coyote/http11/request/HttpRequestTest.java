package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void 요청을_읽고_생성한다() {
        // given
        String requestLine = "GET /index.html HTTP/1.1 ";
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ");
        String requestBody = String.join(System.lineSeparator(),
                "account=gugu&password=password&email=hkkang@woowahan.com");

        // when
        HttpRequest request = HttpRequest.of(requestLine, requestHeader, requestBody);

        // then
        assertThat(request).usingRecursiveComparison()
                .isEqualTo(
                        new HttpRequest(
                                RequestLine.from("GET /index.html HTTP/1.1 "),
                                RequestHeader.from(String.join(System.lineSeparator(),
                                        "Host: localhost:8080 ",
                                        "Connection: keep-alive ")),
                                RequestBody.from(String.join(System.lineSeparator(),
                                        "account=gugu&password=password&email=hkkang@woowahan.com"))
                        )
                );
    }
}
