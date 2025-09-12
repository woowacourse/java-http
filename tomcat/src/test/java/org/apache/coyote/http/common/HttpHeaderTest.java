package org.apache.coyote.http.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @Test
    @DisplayName("원시 헤더 파싱 및 조회 - 대소문자 무시")
    void parseAndGet_caseInsensitive() {
        // given
        final String raw = String.join(HttpConstants.CRLF,
                "Host: localhost:8080",
                "content-type: text/html;charset=UTF-8",
                "Content-Length: 42");

        // when
        final HttpHeader header = HttpHeader.from(raw);

        // then
        assertSoftly(softly -> {
            softly.assertThat(header.get("host")).isEqualTo("localhost:8080");
            softly.assertThat(header.get("HOST")).isEqualTo("localhost:8080");
            softly.assertThat(header.get("Content-Type")).isEqualTo("text/html;charset=UTF-8");
            softly.assertThat(header.get("X-None")).isEqualTo("");
        });
    }

    @Test
    @DisplayName("Content-Length 파싱 - 정상/누락/잘못된 값")
    void getContentLength_variants() {
        // given
        final String ok = String.join(HttpConstants.CRLF, "Content-Length: 12");
        final String bad = String.join(HttpConstants.CRLF, "Content-Length: nope");
        final String none = String.join(HttpConstants.CRLF, "Host: localhost");

        // when
        final HttpHeader headerOk = HttpHeader.from(ok);
        final HttpHeader headerBad = HttpHeader.from(bad);
        final HttpHeader headerNone = HttpHeader.from(none);

        // then
        assertSoftly(softly -> {
            softly.assertThat(headerOk.getContentLength()).isEqualTo(12);
            softly.assertThat(headerBad.getContentLength()).isEqualTo(0);
            softly.assertThat(headerNone.getContentLength()).isEqualTo(0);
        });
    }

    @Test
    @DisplayName("Content-Type 파싱 - ContentType enum 반환")
    void getContentType_fromHeader() {
        final String raw = String.join(HttpConstants.CRLF,
                "content-type: application/x-www-form-urlencoded");
        assertThat(HttpHeader.from(raw).getContentType())
                .isEqualTo(ContentType.FORM_URLENCODED);
    }

    @Test
    @DisplayName("빈/널 원시 헤더는 예외")
    void from_throwsOnEmptyOrNull() {
        assertThatThrownBy(() -> HttpHeader.from(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null이거나 비어있을 수 없습니다");
        assertThatThrownBy(() -> HttpHeader.from((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("toString은 헤더를 Capitalize 하여 CRLF로 연결")
    void toString_capitalizesAndJoins() {
        // given
        final Map<String, String> map = new HashMap<>();
        map.put("content-type", "text/html;charset=UTF-8");
        map.put("content-length", "5");

        // when
        final String s = HttpHeader.from(map).toString();

        // then (순서는 보장하지 않으므로 포함 여부만 검증)
        assertSoftly(softly -> {
            softly.assertThat(s).contains("Content-Type: text/html;charset=UTF-8" + HttpConstants.CRLF);
            softly.assertThat(s).contains("Content-Length: 5" + HttpConstants.CRLF);
        });
    }
}
