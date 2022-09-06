package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @Test
    void apply() {
        final String statusFormat = HttpHeader.HTTP_1_1_STATUS_CODE.apply("200 OK");
        final String contentTypeFormat = HttpHeader.CONTENT_TYPE.apply("application/x-www-form-urlencoded");
        assertAll(
                () -> assertThat(statusFormat).isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(contentTypeFormat).isEqualTo("Content-Type: application/x-www-form-urlencoded;charset=utf-8 ")
        );
    }
}