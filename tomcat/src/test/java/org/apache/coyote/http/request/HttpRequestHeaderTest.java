package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.apache.coyote.http.common.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    @Test
    @DisplayName("기본 헤더 파싱")
    void parseBasicHeaders() {
        // given
        final String rawHeader = String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: text/html");

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertSoftly(softly -> {
            softly.assertThat(header.get("Host")).isEqualTo("localhost:8080");
            softly.assertThat(header.get("Connection")).isEqualTo("keep-alive");
            softly.assertThat(header.get("Accept")).isEqualTo("text/html");
        });
    }

    @Test
    @DisplayName("대소문자 구분 없는 헤더 이름")
    void parseHeadersCaseInsensitive() {
        // given
        final String rawHeader = "Host: localhost:8080";

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertSoftly(softly -> {
            softly.assertThat(header.get("Host")).isEqualTo("localhost:8080");
            softly.assertThat(header.get("host")).isEqualTo("localhost:8080");
            softly.assertThat(header.get("HOST")).isEqualTo("localhost:8080");
        });
    }

    @Test
    @DisplayName("쿠키 헤더 파싱")
    void parseCookieHeader() {
        // given
        final String rawHeader = "Cookie: JSESSIONID=ABC123; theme=dark; lang=ko";

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertSoftly(softly -> {
            softly.assertThat(header.getCookie("JSESSIONID")).isEqualTo("ABC123");
            softly.assertThat(header.getCookie("theme")).isEqualTo("dark");
            softly.assertThat(header.getCookie("lang")).isEqualTo("ko");
        });
    }

    @Test
    @DisplayName("쿠키가 없는 헤더")
    void parseHeaderWithoutCookies() {
        // given
        final String rawHeader = "Host: localhost:8080";

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertSoftly(softly -> {
            softly.assertThat(header.getCookie("JSESSIONID")).isNull();
            softly.assertThat(header.getCookie("nonexistent")).isNull();
        });
    }

    @Test
    @DisplayName("Content-Type 헤더 파싱")
    void parseContentTypeHeader() {
        // given
        final String rawHeader = String.join("\r\n",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded");

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertThat(header.getContentType()).isEqualTo(ContentType.FORM_URLENCODED);
    }

    @Test
    @DisplayName("Content-Length 헤더 파싱")
    void parseContentLengthHeader() {
        // given
        final String rawHeader = String.join("\r\n",
                "Host: localhost:8080",
                "Content-Length: 52");

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertThat(header.getContentLength()).isEqualTo(52);
    }

    @Test
    @DisplayName("잘못된 Content-Length 헤더")
    void parseInvalidContentLengthHeader() {
        // given
        final String rawHeader = String.join("\r\n",
                "Host: localhost:8080",
                "Content-Length: invalid");

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertThat(header.getContentLength()).isEqualTo(0);
    }

    @Test
    @DisplayName("빈 헤더 문자열")
    void parseEmptyHeader() {
        // when & then
        assertThatThrownBy(() -> HttpRequestHeader.from(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP 요청 헤더는 null이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("null 헤더 문자열")
    void parseNullHeader() {
        // when & then
        assertThatThrownBy(() -> HttpRequestHeader.from(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP 요청 헤더는 null이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("헤더 값에 공백이 포함된 경우")
    void parseHeaderWithSpacesInValue() {
        // given
        final String rawHeader = "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)";

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertThat(header.get("User-Agent")).isEqualTo("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
    }

    @Test
    @DisplayName("여러 줄 헤더 파싱")
    void parseMultiLineHeaders() {
        // given
        final String rawHeader = String.join("\r\n",
                "Host: localhost:8080",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "Accept-Language: en-US,en;q=0.5",
                "Accept-Encoding: gzip, deflate");

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertSoftly(softly -> {
            softly.assertThat(header.get("Host")).isEqualTo("localhost:8080");
            softly.assertThat(header.get("Accept")).isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            softly.assertThat(header.get("Accept-Language")).isEqualTo("en-US,en;q=0.5");
            softly.assertThat(header.get("Accept-Encoding")).isEqualTo("gzip, deflate");
        });
    }

    @Test
    @DisplayName("잘못된 형식의 헤더 라인 무시")
    void ignoreInvalidHeaderLine() {
        // given
        final String rawHeader = String.join("\r\n",
                "Host: localhost:8080",
                "InvalidHeaderWithoutColon",
                "Valid-Header: valid-value");

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertSoftly(softly -> {
            softly.assertThat(header.get("Host")).isEqualTo("localhost:8080");
            softly.assertThat(header.get("Valid-Header")).isEqualTo("valid-value");
            softly.assertThat(header.get("InvalidHeaderWithoutColon")).isEmpty();
        });
    }

    @Test
    @DisplayName("콜론으로만 이루어진 헤더 라인 무시")
    void ignoreColonOnlyHeaderLine() {
        // given
        final String rawHeader = String.join("\r\n",
                "Host: localhost:8080",
                ":",
                "Content-Type: application/json");

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertSoftly(softly -> {
            softly.assertThat(header.get("Host")).isEqualTo("localhost:8080");
            softly.assertThat(header.getContentType()).isEqualTo(ContentType.JSON);
        });
    }

    @Test
    @DisplayName("빈 헤더 값 처리")
    void parseEmptyHeaderValue() {
        // given
        final String rawHeader = String.join("\r\n",
                "Host: localhost:8080",
                "Empty-Header: ",
                "Another-Header: value");

        // when
        final HttpRequestHeader header = HttpRequestHeader.from(rawHeader);

        // then
        assertSoftly(softly -> {
            softly.assertThat(header.get("Host")).isEqualTo("localhost:8080");
            softly.assertThat(header.get("Empty-Header")).isEmpty();
            softly.assertThat(header.get("Another-Header")).isEqualTo("value");
        });
    }
}
