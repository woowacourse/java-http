package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeaderTest {

    @DisplayName("response header의 형식을 검증한다.")
    @Test
    void validateHeaderFormat() {
        final ResponseHeader responseHeader = new ResponseHeader("/");

        assertThat(responseHeader.getHeader("Hello world!"))
                .isEqualTo(String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: 12 ",
                        ""));
    }
}
