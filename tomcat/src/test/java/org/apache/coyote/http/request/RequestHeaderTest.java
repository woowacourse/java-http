package org.apache.coyote.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHeaderTest {

    private final List<String> headers = List.of(
            "Cookie:JSESSIONID=abcdef;cake=delicious",
            "Content-Length:12");

    private final RequestHeader requestHeader = new RequestHeader(headers);


    @Test
    @DisplayName("쿠키로 세션을 가지고 있으면 참을 반환한다.")
    void hasCookieWithSession() {
        assertThat(requestHeader.hasCookieWithSession()).isTrue();
    }

    @Test
    @DisplayName("콘텐츠 길이를 반환한다.")
    void getContentLength() {
        assertThat(requestHeader.getContentLength()).isEqualTo(12);
    }

    @Test
    @DisplayName("첫 번째 콜론을 기준으로 key 와 value 를 구분한다.")
    void validate() {
        List<String> headerValue = List.of("", "test:complete", "but:fail", "by:this:content");
        RequestHeader header = new RequestHeader(headerValue);

        assertThat(header.hasHeader("test")).isTrue();
        assertThat(header.hasHeader("by")).isTrue();
    }
}
