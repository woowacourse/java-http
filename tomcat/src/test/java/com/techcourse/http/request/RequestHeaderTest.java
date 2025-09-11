package com.techcourse.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http.request.RequestHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeaderTest {

    @DisplayName("request header 생성")
    @Test
    void fromTest1() {
        // given
        List<String> headerStrings = List.of(
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 25"
        );

        // when
        RequestHeader requestHeader = RequestHeader.from(headerStrings);

        // then
        assertThat(requestHeader.hasContentLengthKey()).isTrue();
        assertThat(requestHeader.getContentLength()).isEqualTo(25);
    }

    @DisplayName("잘못된 헤더 형식인 경우")
    @Test
    void fromTest2() {
        // given
        List<String> headerStrings = List.of("Invalid-Header-Format");

        // when & then
        assertThatThrownBy(() -> RequestHeader.from(headerStrings))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("request header의 형식은 'key: value' 이여야 합니다.");
    }

    @DisplayName("Content Length 키가 있을 때")
    @Test
    void hasContentLengthKeyTest1() {
        // given
        Map<String, String> values = new ConcurrentHashMap<>();
        values.put("Content-Length", "25");
        RequestHeader requestHeader = new RequestHeader(values);

        // when & then
        assertThat(requestHeader.hasContentLengthKey()).isTrue();
    }

    @DisplayName("Content Length 키가 없을 때")
    @Test
    void hasContentLengthKeyTest2() {
        // given
        Map<String, String> values = new ConcurrentHashMap<>();
        values.put("Host", "localhost:8080");
        RequestHeader requestHeader = new RequestHeader(values);

        // when & then
        assertThat(requestHeader.hasContentLengthKey()).isFalse();
    }

    @DisplayName("Cookie 키가 있을 때")
    @Test
    void hasCookieKeyTest1() {
        // given
        Map<String, String> values = new ConcurrentHashMap<>();
        values.put("Cookie", "JSESSIONID=abc123");
        RequestHeader requestHeader = new RequestHeader(values);

        // when & then
        assertThat(requestHeader.hasCookieKey()).isTrue();
    }

    @DisplayName("Cookie 키가 있을 때")
    @Test
    void hasCookieKeyTest2() {
        // given
        Map<String, String> values = new ConcurrentHashMap<>();
        values.put("Host", "localhost:8080");
        RequestHeader requestHeader = new RequestHeader(values);

        // when & then
        assertThat(requestHeader.hasCookieKey()).isFalse();
    }
}
