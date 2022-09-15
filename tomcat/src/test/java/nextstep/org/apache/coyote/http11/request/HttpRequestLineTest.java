package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.header.HttpVersion;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {

    @DisplayName("요청 데이터를 받아서 request line을 생성할 수 있다")
    @Test
    void createHttpRequestLine() {
        final String request = "GET /index.html HTTP/1.1";

        final HttpRequestLine requestLine = HttpRequestLine.from(request);

        assertAll(
                () -> assertThat(requestLine.getPath()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getVersion()).isEqualTo(HttpVersion.HTTP_1_1)
        );
    }

    @DisplayName("요청한 HttpMethod와 같은 HttpMethod인지 확인한다")
    @Test
    void checkSameMethod() {
        final String request = "GET /index.html HTTP/1.1";
        final HttpRequestLine requestLine = HttpRequestLine.from(request);

        assertThat(requestLine.isSameMethod(HttpMethod.GET)).isTrue();
    }
}
