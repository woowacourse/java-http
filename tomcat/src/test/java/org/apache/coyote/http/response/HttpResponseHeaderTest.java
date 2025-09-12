package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.apache.coyote.http.common.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseHeaderTest {

    @Test
    @DisplayName("ContentType으로 ResponseHeader 생성")
    void createWithContentType() {
        // given
        final ContentType contentType = ContentType.HTML;

        // when
        final HttpResponseHeader header = HttpResponseHeader.withContentType(contentType);

        // then
        assertThat(header.toString()).contains("Content-Type: text/html;charset=UTF-8");
    }

    @Test
    @DisplayName("일반 헤더 추가")
    void addHeader() {
        // given
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.HTML);

        // when
        header.add("Custom-Header", "custom-value");

        // then
        assertThat(header.toString()).contains("Custom-Header: custom-value");
    }

    @Test
    @DisplayName("Set-Cookie 헤더 추가")
    void addSetCookie() {
        // given
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.HTML);

        // when
        header.addSetCookie("sessionId", "ABC123");

        // then
        assertThat(header.toString()).contains("Set-Cookie: sessionId=ABC123");
    }

    @Test
    @DisplayName("여러 쿠키 추가시 마지막 쿠키만 유지")
    void addMultipleCookies() {
        // given
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.HTML);

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

    @Test
    @DisplayName("JavaScript ContentType으로 ResponseHeader 생성")
    void createWithJavaScriptContentType() {
        // when
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.JAVASCRIPT);

        // then
        assertThat(header.toString()).contains("Content-Type: application/javascript;charset=UTF-8");
    }

    @Test
    @DisplayName("CSS ContentType으로 ResponseHeader 생성")
    void createWithCssContentType() {
        // when
        final HttpResponseHeader header = HttpResponseHeader.withContentType(ContentType.CSS);

        // then
        assertThat(header.toString()).contains("Content-Type: text/css");
    }
}
