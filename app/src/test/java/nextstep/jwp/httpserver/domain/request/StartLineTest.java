package nextstep.jwp.httpserver.domain.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.domain.HttpMethod;
import nextstep.jwp.httpserver.domain.HttpVersion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StartLine 단위 테스트")
class StartLineTest {

    @Test
    @DisplayName("Http request start line 나누기")
    void from() {
        // given
        String firstLine = "GET /index.html HTTP/1.1";

        // when
        StartLine startLine = StartLine.from(firstLine);

        // then
        assertThat(startLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(startLine.getRequestTarget()).isEqualTo("/index.html");
        assertThat(startLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    @DisplayName("HTTP Method가 POST가 아닌 경우")
    void isPost() {
        // given
        String firstLine = "POST /login HTTP/1.1";
        StartLine startLine = StartLine.from(firstLine);

        // when
        boolean isPost = startLine.isPost();

        // then
        assertTrue(isPost);
    }

    @Test
    @DisplayName("HTTP Method가 POST가 아닌 경우")
    void isNotPost() {
        // given
        String firstLine = "GET /index.html HTTP/1.1";
        StartLine startLine = StartLine.from(firstLine);

        // when
        boolean isPost = startLine.isPost();

        // then
        assertFalse(isPost);
    }
}
