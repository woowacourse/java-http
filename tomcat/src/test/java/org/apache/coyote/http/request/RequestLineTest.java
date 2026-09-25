package org.apache.coyote.http.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.apache.coyote.http.HttpVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestLineTest {

    @Test
    void 요청_라인을_파싱한다() {
        final RequestLine requestLine = RequestLine.from("GET /hello/world?param=1 HTTP/1.1");

        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getUri().getPath()).isEqualTo("/hello/world");
        assertThat(requestLine.getUri().getRawQuery()).isEqualTo("param=1");
        assertThat(requestLine.getVersion()).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "GET /hello",               // 조각 부족
            "GET  /hello HTTP/1.1",     // 공백 두 개
            "get /hello HTTP/1.1",      // 소문자 메서드
            "GET /hello HTTP/11",       // 버전 형식 오류
            "GET /hello 1.1"            // HTTP/ 접두사 없음
    })
    void 잘못된_요청_라인은_예외가_발생한다(final String firstLine) {
        assertThatThrownBy(() -> RequestLine.from(firstLine))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
