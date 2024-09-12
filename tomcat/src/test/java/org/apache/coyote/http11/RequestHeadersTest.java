package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RequestHeadersTest {

    @DisplayName("Request Header를 파싱하는데 성공한다.")
    @Test
    void parseRequestHeader() {
        // given
        List<String> rawRequestHeaders = List.of(
                "Cookie: JSESSIONID=f123234234;",
                "Content-Length: 53 ",
                "Accept: someValue",
                "Host: localhost"
        );
        // when
        RequestHeaders requestHeaders = new RequestHeaders(rawRequestHeaders);
        String actual = requestHeaders.getHeaderValue("Cookie");

        // then
        assertThat(actual).isEqualTo("JSESSIONID=f123234234;");
    }

    @DisplayName("Request 값이 없을 경우 빈 값을 반환한다.")
    @Test
    void getEmptyHeaderValue() {
        // given
        List<String> rawRequestHeaders = List.of();

        // when
        RequestHeaders requestHeaders = new RequestHeaders(rawRequestHeaders);
        String actual = requestHeaders.getHeaderValue("Cookie");

        // then
        assertThat(actual).isEqualTo("");
    }

    @DisplayName("header 조회 시 case-insensitive 하게 처리되어야 한다.")
    @Test
    void getHeaderValue_WithCaseInsensitive() {
        // given
        List<String> rawRequestHeaders = List.of(
                "Cookie: JSESSIONID=f123234234;",
                "Content-Length: 53 ",
                "Accept: someValue",
                "Host: localhost"
        );
        RequestHeaders requestHeaders = new RequestHeaders(rawRequestHeaders);

        // when
        String expect = requestHeaders.getHeaderValue("AcCept");
        String actual = requestHeaders.getHeaderValue("accept");

        // then
        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("header 추출 시 \":\" 로 구분하고 각 예외사항을 고려한다.")
    @Test
    void parseRequestHeaderWithSpecialCase() {
        // given
        List<String> rawRequestHeaders = List.of(
                "headerA:    value;   ",
                "Content-Length:   53 ",
                "emptyHeader:"
        );
        RequestHeaders requestHeaders = new RequestHeaders(rawRequestHeaders);

        // when
        String headerA = requestHeaders.getHeaderValue("headerA");
        String contentLength = requestHeaders.getHeaderValue("Content-Length");
        String emptyHeader = requestHeaders.getHeaderValue("emptyHeader");

        // then
        assertAll(
                () -> assertThat(headerA).isEqualTo("value;"),
                () -> assertThat(contentLength).isEqualTo("53"),
                () -> assertThat(emptyHeader).isEmpty()
        );
    }
}
