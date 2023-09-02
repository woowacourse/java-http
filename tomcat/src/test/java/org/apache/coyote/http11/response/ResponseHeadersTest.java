package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ResponseHeadersTest {

    @Test
    void 응답헤더를_생성한다2() {
        // given
        ResponseHeaders responseHeaders = ResponseHeaders.of(ResponseBody.of("Hello, World!", "html"));

        // when
        String response = responseHeaders.toString();

        // then
        assertThat(response).isEqualTo("Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 13 \r\n");
    }

    @Test
    void redirect_응답_헤더를_생성한다() {
        // given
        ResponseHeaders responseHeaders = ResponseHeaders.ofRedirect("/index.html");

        // when
        String response = responseHeaders.toString();

        // then
        assertThat(response).isEqualTo("Location: /index.html \r\n");
    }
}
