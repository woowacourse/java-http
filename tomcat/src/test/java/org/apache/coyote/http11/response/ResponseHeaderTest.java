package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.ContentType.HTML;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeaderTest {

    @DisplayName("Response 규격에 맞게 문자열로 변환한다.")
    @Test
    void toResponseValue() {
        // given
        ResponseHeader responseHeader = ResponseHeader.empty();
        responseHeader.put("Content-Type", HTML.value());
        responseHeader.put("Content-Length", "5564");

        // when
        String actual = responseHeader.toResponseValue();
        String expected = String.join("\r\n",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                ""
        );

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
