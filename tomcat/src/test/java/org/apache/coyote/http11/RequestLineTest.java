package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("요청 라인에서 메서드, 경로, HTTP 버전을 추출한다")
    void parse() {
        RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
        assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    @DisplayName("경로와 쿼리 스트링을 분리한다")
    void separateQueryString() {
        RequestLine requestLine = RequestLine.from("GET /login?account=gugu&password=1234 HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getQueryParameters().get("account")).contains("gugu");
        assertThat(requestLine.getQueryParameters().get("password")).contains("1234");
    }

    @Test
    @DisplayName("쿼리 스트링이 없으면 빈 파라미터를 가진다")
    void noQueryString() {
        RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        assertThat(requestLine.getQueryParameters().isEmpty()).isTrue();
    }

    @Nested
    @DisplayName("HTTP 메서드 판별")
    class MethodCheck {

        @Test
        void GET_요청이면_isGet이_참이다() {
            RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

            assertThat(requestLine.isGet()).isTrue();
            assertThat(requestLine.isPost()).isFalse();
        }

        @Test
        void POST_요청이면_isPost가_참이다() {
            RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1");

            assertThat(requestLine.isPost()).isTrue();
            assertThat(requestLine.isGet()).isFalse();
        }
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드면 예외가 발생한다")
    void unsupportedMethod() {
        assertThatThrownBy(() -> RequestLine.from("TRACE /index.html HTTP/1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
