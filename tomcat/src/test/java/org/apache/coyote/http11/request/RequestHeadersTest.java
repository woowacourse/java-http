package org.apache.coyote.http11.request;

import org.apache.coyote.http11.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestHeadersTest {

    @Test
    void 헤더_이름은_대소문자를_구분하지_않는다() {
        final RequestHeaders headers = RequestHeaders.from(List.of("content-length: 5"));

        assertThat(headers.getContentLength()).isEqualTo(5);
    }

    @Test
    void 대소문자가_달라도_Content_Length_중복을_거부한다() {
        assertThatThrownBy(() -> RequestHeaders.from(List.of("Content-Length: 5", "content-length: 5")))
                .isInstanceOf(BadRequestException.class);
    }
}