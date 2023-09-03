package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeaderTest {

    @Test
    @DisplayName("ResponseHeader를 생성한다.")
    void createResponseHeader() {
        //given
        final ContentType contentType = ContentType.HTML;
        final int contentLength = 10;

        //when
        final ResponseHeader responseHeader = ResponseHeader.of(contentType, contentLength);

        //then
        final String expected = "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: 10 ";
        assertThat(responseHeader.parse()).isEqualTo(expected);
    }

}
