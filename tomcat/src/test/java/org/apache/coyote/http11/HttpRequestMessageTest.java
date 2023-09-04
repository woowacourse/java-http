package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.handler.component.HttpRequestMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestMessageTest {

    @DisplayName("빈 요청 메시지를 통해 생성시 오류를 던진다.")
    @Test
    void create_fail_emptyMessage() {
        //given
        final List<String> messageLines = Collections.emptyList();

        //when
        //then
        assertThatThrownBy(() -> HttpRequestMessage.with(messageLines))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("유효하지 않는 start-line 을 가진 요청 메시지로 생성시 오류를 던진다.")
    @Test
    void create_fail_invalidStartLine() {
        //given
        final List<String> messageLines = List.of("GET/hello HTTP/1.1");

        //when
        //then
        assertThatThrownBy(() -> HttpRequestMessage.with(messageLines))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Headers 를 조회한다.")
    @Test
    void getHeaders() {
        //given
        final List<String> messageLines = List.of(
            "GET /hihi HTTP/1.1",
            "Host: localhost:8080\n",
            "Accept: text/css,*/*;q=0.1\n",
            "Connection: keep-alive",
            "",
            "body"
        );

        //when
        final HttpRequestMessage httpRequestMessage = HttpRequestMessage.with(messageLines);

        //then
        final Map<String, String> expect = Map.of(
            "host", "localhost:8080",
            "accept", "text/css,*/*;q=0.1",
            "connection", "keep-alive"
        );

        assertThat(httpRequestMessage.getHeaders()).usingRecursiveComparison().isEqualTo(expect);
    }

    @DisplayName("Headers 가 없는 경우 빈 Map 을 반환한다.")
    @Test
    void emptyHeaders() {
        //given
        final List<String> messageLines = List.of("GET /hello HTTP/1.1");
        final HttpRequestMessage httpRequestMessage = HttpRequestMessage.with(messageLines);

        //when
        final Map<String, String> headers = httpRequestMessage.getHeaders();

        //then
        assertThat(headers).isEmpty();
    }

    @DisplayName("원하는 헤더 값만을 조회한다.")
    @Nested
    class ParseHeader {

        @DisplayName("찾으려는 헤더가 존재할 때 ( 대소문자 무시 )")
        @Test
        void headerExist() {
            //given
            final List<String> messageLines = List.of(
                "GET /hihi HTTP/1.1",
                "Host: localhost:8080\n",
                "Accept: text/css,*/*;q=0.1\n",
                "Connection: keep-alive",
                "",
                "body"
            );
            final HttpRequestMessage httpRequestMessage = HttpRequestMessage.with(messageLines);

            //when
            final Optional<String> headerValue = httpRequestMessage.getHeader("hOst");

            //then
            assertThat(headerValue)
                .isPresent()
                .hasValue("localhost:8080");
        }

        @DisplayName("찾으려는 헤더가 없을 때")
        @Test
        void headerNotExist() {
            //given
            final List<String> messageLines = List.of(
                "GET /hihi HTTP/1.1",
                "Host: localhost:8080\n",
                "Accept: text/css,*/*;q=0.1\n",
                "Connection: keep-alive",
                "",
                "body"
            );
            final HttpRequestMessage httpRequestMessage = HttpRequestMessage.with(messageLines);

            //when
            final Optional<String> headerValue = httpRequestMessage.getHeader("Content-Type");

            //then
            assertThat(headerValue).isEmpty();
        }
    }

    @DisplayName("Start-line 에서 요청 대상을 추출한다.")
    @Test
    void getTargetUrl() {
        //given
        final List<String> messageLines = List.of("GET /hello HTTP/1.1");
        final HttpRequestMessage httpRequestMessage = HttpRequestMessage.with(messageLines);

        //when
        final String targetUrl = httpRequestMessage.getTargetUrl();

        //then
        final String expect = "/hello";
        assertThat(targetUrl).isEqualTo(expect);
    }
}
