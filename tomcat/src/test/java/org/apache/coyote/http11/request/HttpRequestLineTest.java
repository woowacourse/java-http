package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.HttpMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {

    @Test
    @DisplayName("정적 팩토리 메소드는 입력을 HttpMethod, Uri, QueryParams로 파싱하여 저장한다.")
    void of() {
        // given
        final String rawStartLine = "GET /path?name=eve HTTP/1.1";

        // when
        final HttpRequestLine requestLine = HttpRequestLine.of(rawStartLine);

        // then
        assertAll(() -> {
            assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
            assertThat(requestLine.getPath()).isEqualTo("/path");
            assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
            assertThat(requestLine.getQueryParams())
                    .extractingByKey("name").isEqualTo("eve");
        });
    }
}