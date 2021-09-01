package nextstep.jwp.httpserver.domain.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.domain.HttpVersion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("StartLine 단위 테스트")
class RequestLineTest {

    @Test
    @DisplayName("Http request start line 나누기")
    void from() {
        // given
        String firstLine = "GET /index.html HTTP/1.1";

        // when
        RequestLine requestLine = RequestLine.from(firstLine);

        // then
        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getRequestTarget()).isEqualTo("/index.html");
        assertThat(requestLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    @DisplayName("HTTP Method가 POST인 경우")
    void isPost() {
        // given
        String firstLine = "POST /login HTTP/1.1";
        RequestLine requestLine = RequestLine.from(firstLine);

        // when
        boolean isPost = requestLine.isPost();

        // then
        assertTrue(isPost);
    }

    @Test
    @DisplayName("HTTP Method가 POST가 아닌 경우")
    void isNotPost() {
        // given
        String firstLine = "GET /index.html HTTP/1.1";
        RequestLine requestLine = RequestLine.from(firstLine);

        // when
        boolean isPost = requestLine.isPost();

        // then
        assertFalse(isPost);
    }

    @Test
    @DisplayName("HTTP Method가 GET인 경우")
    void isGet() {
        // given
        String firstLine = "GET /login HTTP/1.1";
        RequestLine requestLine = RequestLine.from(firstLine);

        // when
        boolean isGet = requestLine.isGet();

        // then
        assertTrue(isGet);
    }

    @Test
    @DisplayName("HTTP Method가 GET이 아닌 경우")
    void isNotGet() {
        // given
        String firstLine = "POST /index.html HTTP/1.1";
        RequestLine requestLine = RequestLine.from(firstLine);

        // when
        boolean isGet = requestLine.isGet();

        // then
        assertFalse(isGet);
    }
}
