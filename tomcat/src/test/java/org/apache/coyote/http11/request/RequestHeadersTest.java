package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHeadersTest {

    @Test
    @DisplayName("헤더 라인은 이름과 값으로 분리된다.")
    void parseHeaderLines() {
        // given
        List<String> lines = List.of(
                "Host: localhost:8080",
                "Accept: text/html"
        );

        // when
        RequestHeaders requestHeaders = RequestHeaders.from(lines);

        // then
        assertThat(requestHeaders.getValue("Host")).isEqualTo("localhost:8080");
        assertThat(requestHeaders.getValue("Accept")).isEqualTo("text/html");
    }

    @Test
    @DisplayName("존재하지 않는 헤더를 조회하면 null을 반환한다.")
    void invalidNameReturnNull() {
        // given
        RequestHeaders requestHeaders = RequestHeaders.from(List.of("Host: localhost:8080"));

        // when
        String actual = requestHeaders.getValue("Accept");

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("헤더가 하나도 없으면 조회 결과가 비어있다.")
    void emptyHeaders() {
        // given
        RequestHeaders requestHeaders = RequestHeaders.from(List.of());

        // when & then
        assertThat(requestHeaders.getValue("Host")).isNull();
        assertThat(requestHeaders.getContentType()).isNull();
        assertThat(requestHeaders.getCookie()).isNull();
        assertThat(requestHeaders.getContentLength()).isZero();
    }

    @Test
    @DisplayName("구분자가 없는 라인은 무시한다.")
    void ignoreMalformedLine() {
        // given
        List<String> lines = List.of(
                "Host localhost:8080",
                "Accept: text/html"
        );

        // when
        RequestHeaders requestHeaders = RequestHeaders.from(lines);

        // then
        assertThat(requestHeaders.getValue("Host")).isNull();
        assertThat(requestHeaders.getValue("Accept")).isEqualTo("text/html");
    }

    @Test
    @DisplayName("Content-Length 헤더를 숫자로 반환한다.")
    void getContentLength() {
        // given
        RequestHeaders requestHeaders = RequestHeaders.from(List.of("Content-Length: 30"));

        // when
        int actual = requestHeaders.getContentLength();

        // then
        assertThat(actual).isEqualTo(30);
    }

    @Test
    @DisplayName("Content-Length 헤더가 없으면 0을 반환한다.")
    void getContentLengthWithoutHeader() {
        // given
        RequestHeaders requestHeaders = RequestHeaders.from(List.of("Host: localhost:8080"));

        // when
        int actual = requestHeaders.getContentLength();

        // then
        assertThat(actual).isZero();
    }

    @Test
    @DisplayName("Content-Type 헤더를 반환한다.")
    void getContentType() {
        // given
        RequestHeaders requestHeaders = RequestHeaders.from(
                List.of("Content-Type: application/x-www-form-urlencoded"));

        // when
        String actual = requestHeaders.getContentType();

        // then
        assertThat(actual).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    @DisplayName("Cookie 헤더를 반환한다.")
    void getCookie() {
        // given
        RequestHeaders requestHeaders = RequestHeaders.from(List.of("Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"));

        // when
        String actual = requestHeaders.getCookie();

        // then
        assertThat(actual).isEqualTo("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }

}
