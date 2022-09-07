package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.LinkedList;
import java.util.Queue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Nested
    @DisplayName("정적 팩토리 메소드는")
    class Of {

        @Test
        @DisplayName("body가 form data 형식이 아니라면 쿼리 스트링을 파싱하여 QueryParameter에 저장한다.")
        void success_queryString() {
            // given
            final HttpRequestLine httpRequestLine = HttpRequestLine.of("GET /path?id=yujeong HTTP/1.1");
            final Queue<String> rawRequest = new LinkedList<>();
            rawRequest.add("name: eve");
            final HttpRequestHeader httpRequestHeader = HttpRequestHeader.of(rawRequest);

            // when
            final HttpRequest httpRequest = HttpRequest.of(httpRequestLine, httpRequestHeader, "");

            // then
            assertAll(() -> {
                        assertThat(httpRequest.getPath()).isEqualTo("/path");
                        assertThat(httpRequest.getParameter("id")).isEqualTo("yujeong");
                        assertThat(httpRequest.getHeader("name")).isEqualTo("eve");
                    }
            );
        }

        @Test
        @DisplayName("form data 형식의 body를 입력 받으면 파싱하여 QueryParameter에 저장한다.")
        void success_formDataBody() {
            // given
            final HttpRequestLine httpRequestLine = HttpRequestLine.of("GET /path HTTP/1.1");
            final Queue<String> rawRequest = new LinkedList<>();
            rawRequest.add("name: eve");
            rawRequest.add("Content-Type: application/x-www-form-urlencoded");
            final HttpRequestHeader httpRequestHeader = HttpRequestHeader.of(rawRequest);

            // when
            final HttpRequest httpRequest = HttpRequest.of(httpRequestLine, httpRequestHeader, "id=yujeong");

            // then
            assertAll(() -> {
                        assertThat(httpRequest.getPath()).isEqualTo("/path");
                        assertThat(httpRequest.getParameter("id")).isEqualTo("yujeong");
                        assertThat(httpRequest.getHeader("name")).isEqualTo("eve");
                    }
            );
        }
    }
}