package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("헤더의 필드가 1개일 때, 해당 필드를 반환한다.")
    void getFieldByHeaderNameTest() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setHeader(HttpHeaderName.CONTENT_TYPE, "text/plain");

        // when
        Optional<String> contentType = headers.getFieldByHeaderName(HttpHeaderName.CONTENT_TYPE);

        // then
        assertThat(contentType).contains("text/plain");
    }

    @Test
    @DisplayName("헤더의 필드가 1개보다 많을 때, 예외를 던진다.")
    void getFieldByHeaderNameWithMultipleFieldsTest() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setHeader(HttpHeaderName.CONTENT_TYPE, "text/plain");
        headers.setHeader(HttpHeaderName.CONTENT_TYPE, "text/html");

        // when, then
        assertThatThrownBy(() -> headers.getFieldByHeaderName(HttpHeaderName.CONTENT_TYPE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("헤더의 필드가 1개보다 많습니다.");
    }
}
