package org.apache.catalina.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHeaderTest {

    @DisplayName("RequestHeader를 파싱한다.")
    @Test
    void parse() {
        List<String> headers = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );
        RequestHeader requestHeader = RequestHeader.parse(headers);

        assertThat(requestHeader.getCookie()).isEqualTo("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @DisplayName("RequestHeader에 ContentLength 값이 있다면 참을 반환한다.")
    @Test
    void notContainsContentLength() {
        List<String> headers = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*"
        );
        RequestHeader requestHeader = RequestHeader.parse(headers);

        assertThat(requestHeader.notContainsContentLength()).isTrue();
    }

    @DisplayName("RequestHeader에 ContentLength 값이 있다면 거짓을 반환한다.")
    @Test
    void containsContentLength() {
        List<String> headers = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Accept: */*"
        );
        RequestHeader requestHeader = RequestHeader.parse(headers);

        assertThat(requestHeader.notContainsContentLength()).isFalse();
    }
}
