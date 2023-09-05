package org.apache.coyote.http11.request;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestStartLineTest {

    @Test
    void request_start_line_생성_테스트() {
        String startLine = "GET /index.html HTTP/1.1";
        HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.of(startLine);

        assertAll(
                () -> Assertions.assertThat(httpRequestStartLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> Assertions.assertThat(httpRequestStartLine.getHttpVersion()).isEqualTo("HTTP/1.1"),
                () -> Assertions.assertThat(httpRequestStartLine.getUri().getPath()).isEqualTo("/index.html")
        );
    }
}
