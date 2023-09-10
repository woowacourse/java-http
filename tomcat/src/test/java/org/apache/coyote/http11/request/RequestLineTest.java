package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestLineTest {

    @Test
    void RequestLine_생성() {
        // given
        String line = "GET /index.html HTTP/1.1";

        // when
        RequestLine requestLine = RequestLine.of(line);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestLine.getMethod()).isEqualTo("GET");
            softly.assertThat(requestLine.getUri()).isEqualTo("/index.html");
            softly.assertThat(requestLine.getProtocol()).isEqualTo("HTTP/1.1");
        });
    }


}
