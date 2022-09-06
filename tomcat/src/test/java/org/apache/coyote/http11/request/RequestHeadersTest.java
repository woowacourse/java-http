package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @Test
    void request_header를_파싱한다() {
        // given
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: 10";

        // when
        RequestHeaders requestHeaders = RequestHeaders.of(List.of(contentType, contentLength));

        // then
        Assertions.assertAll(
                () -> assertThat(requestHeaders.getHeader("Content-Type")).isEqualTo("text/html;charset=utf-8"),
                () -> assertThat(requestHeaders.getHeader("Content-Length")).isEqualTo("10"),
                () -> assertThat(requestHeaders.getContentLength()).isEqualTo(10)
        );
    }

    @Test
    void content_length가_없으면_0을_반환한다() {
        // given
        String contentType = "Content-Type: text/html;charset=utf-8";

        // when
        RequestHeaders requestHeaders = RequestHeaders.of(List.of(contentType));

        // then
        assertThat(requestHeaders.getContentLength()).isEqualTo(0);
    }

    @Test
    void JSESSIONID가_있는지_확인한다() {
        // given
        String requestHeader = "JSESSIONID: eden";

        //when
        RequestHeaders requestHeaders = RequestHeaders.of(List.of(requestHeader));

        // then
        assertThat(requestHeaders.getOrCreateJSessionId()).isEqualTo("eden");
    }

    @Test
    void JSESSIONID가_없으면_UUID를_반환한다() {
        // given
        String requestHeader = "No-JSessionId: eden2";

        //when
        RequestHeaders requestHeaders = RequestHeaders.of(List.of(requestHeader));

        // then
        assertThat(requestHeaders.getOrCreateJSessionId()).containsAnyOf("-");

    }
}
