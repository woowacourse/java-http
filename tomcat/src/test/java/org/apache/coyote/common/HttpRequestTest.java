package org.apache.coyote.common;

import org.apache.coyote.exception.CoyoteHttpException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void BufferedReader를_이용해서_HTTP_요청의_첫_번째_라인에_HTTP버전과_URI와_HTTP메서드가_존재한다면_생성에_성공한다() {
        // given
        final byte[] requestFirstLine = "GET /index.html HTTP/1.1\r\n\r\n ".getBytes(UTF_8);
        final BufferedReader requestReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestFirstLine)));

        // when
        final HttpRequest httpRequest = HttpRequest.from(requestReader);

        // then
        assertAll(
                () -> assertThat(httpRequest.httpMethod().name()).isEqualTo("GET"),
                () -> assertThat(httpRequest.requestUri().getPath()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.httpVersion().version()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    void BufferedReader를_이용해서_HTTP_요청의_첫_번째_라인에_HTTP버전과_URI와_HTTP메서드_중에_HTTP버전이_존재하지_않는다면_예외가_발생한다() {
        // given
        final byte[] requestFirstLine = "GET /index.html\r\n\r\n ".getBytes(UTF_8);
        final BufferedReader requestReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestFirstLine)));

        // expect
        assertThatThrownBy(() -> HttpRequest.from(requestReader))
                .isInstanceOf(CoyoteHttpException.class);
    }

    @Test
    void BufferedReader를_이용해서_HTTP_요청의_첫_번째_라인에_HTTP버전과_URI와_HTTP메서드_중에_URI가_존재하지_않는다면_예외가_발생한다() {
        // given
        final byte[] requestFirstLine = "GET HTTP/1.1\r\n\r\n ".getBytes(UTF_8);
        final BufferedReader requestReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestFirstLine)));

        // expect
        assertThatThrownBy(() -> HttpRequest.from(requestReader))
                .isInstanceOf(CoyoteHttpException.class);
    }

    @Test
    void BufferedReader를_이용해서_HTTP_요청의_첫_번째_라인에_HTTP버전과_URI와_HTTP메서드_중에_HTTP메서드가_존재하지_않는다면_예외가_발생한다() {
        // given
        final byte[] requestFirstLine = "/index.html HTTP/1.1\r\n\r\n ".getBytes(UTF_8);
        final BufferedReader requestReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestFirstLine)));

        // expect
        assertThatThrownBy(() -> HttpRequest.from(requestReader))
                .isInstanceOf(CoyoteHttpException.class);
    }
}
