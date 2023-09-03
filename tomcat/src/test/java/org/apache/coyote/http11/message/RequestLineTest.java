package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("메세지의 start line 으로부터 RequestLine 을 생성한다.")
    void from_success() {
        // given
        final String startLine = "GET / HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(startLine);

        // then
        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getPath()).isEqualTo("/");
    }

    @Test
    @DisplayName("메세지의 start line 요소가 3개가 아니라면 예외가 발생한다.")
    void from_fail() {
        // given
        final String startLine = "GET /";

        // when
        assertThatThrownBy(() -> RequestLine.from(startLine))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잘못된 HTTP 요청입니다.");
    }

    @Test
    @DisplayName("요청 경로로부터 정적 파일의 확장자를 추출한다.")
    void parseFileExtensionFromPath() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /chart-pie.js HTTP/1.1");

        // when
        final Optional<String> extension = requestLine.parseFileExtensionFromPath();

        // then
        assertThat(extension).isPresent()
            .get().isEqualTo("js");
    }

    @Test
    @DisplayName("요청 경로가 정적 파일이 아니라면 Optional.empty() 가 반환된다.")
    void parseFileExtensionFromPath_notStaticFilePath() {
        // given
        final RequestLine requestLine = RequestLine.from("GET / HTTP/1.1");

        // when
        final Optional<String> extension = requestLine.parseFileExtensionFromPath();

        // then
        assertThat(extension).isEmpty();
    }

    @Test
    @DisplayName("QueryString이 존재한다면, URI로부터 QueryString 과 path를 따로 파싱한다.")
    void parseQueryString() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /login?account=gugu&password=password HTTP/1.1");

        // when
        final Optional<String> queryString = requestLine.parseQueryString();
        final String path = requestLine.getPath();

        // then
        assertThat(queryString).isPresent()
            .get().isEqualTo("account=gugu&password=password");
        assertThat(path).isEqualTo("/login");
    }

    @Test
    @DisplayName("QueryString 이 없다면 파싱 결과는 Optional.empty() 가 반환된다.")
    void parseQueryString_notExist() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");

        // when
        final Optional<String> queryString = requestLine.parseQueryString();
        final String path = requestLine.getPath();

        // then
        assertThat(queryString).isEmpty();
        assertThat(path).isEqualTo("/login");
    }
}
