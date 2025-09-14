package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import common.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpResponseHeaderTest {

    @ParameterizedTest(name = "ContentType {0} -> {1}")
    @CsvSource({
            "TEXT_HTML,Content-Type: text/html;charset=UTF-8",
            "APPLICATION_JAVASCRIPT,Content-Type: application/javascript;charset=UTF-8",
            "TEXT_CSS,Content-Type: text/css"
    })
    @DisplayName("ContentType으로 ResponseHeader 생성")
    void createWithContentType(final ContentType contentType, final String expected) {
        final HttpResponseHeader header = HttpResponseHeader.withContentType(contentType);
        assertThat(header.toString()).contains(expected);
    }

    @Test
    @DisplayName("일반 헤더 추가")
    void addHeader() {
        // given
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.TEXT_HTML);

        // when
        header.add("Custom-Header", "custom-value");

        // then
        assertThat(header.toString()).contains("Custom-Header: custom-value");
    }

    @Test
    @DisplayName("Set-Cookie 헤더 추가")
    void addSetCookie() {
        // given
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.TEXT_HTML);

        // when
        header.addSetCookie("sessionId", "ABC123");

        // then
        assertThat(header.toString()).contains("Set-Cookie: sessionId=ABC123");
    }

    @Test
    @DisplayName("여러 쿠키 추가시 마지막 쿠키만 유지")
    void addMultipleCookies() {
        // given
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.TEXT_HTML);

        // when
        header.addSetCookie("first", "value1");
        header.addSetCookie("second", "value2");

        // then
        final String headerString = header.toString();
        assertSoftly(softly -> {
            softly.assertThat(headerString).doesNotContain("Set-Cookie: first=value1");
            softly.assertThat(headerString).contains("Set-Cookie: second=value2");
        });
    }


}
