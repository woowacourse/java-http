package http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;
import org.apache.catalina.exception.InvalidHttpRequestFormatException;
import org.junit.jupiter.api.Test;

class HttpRequestStartLineTest {

    @Test
    void HTTP_요청의_첫_줄_파싱_테스트() {
        // given
        String requestLine = "GET /index.html HTTP/1.1";

        // when
        HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.parse(requestLine);

        // then
        assertAll(
                () -> assertThat(httpRequestStartLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequestStartLine.getUri()).isEqualTo(URI.create("/index.html")),
                () -> assertThat(httpRequestStartLine.getProtocol()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    void HTTP_요청_첫_줄의_형식이_올바르지_않으면_예외를_반환한다() {
        // given
        String requestLine = "invalid";

        // when, then
        assertThatThrownBy(() -> HttpRequestStartLine.parse(requestLine))
                .isExactlyInstanceOf(InvalidHttpRequestFormatException.class);
    }
}
