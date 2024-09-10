package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseHeaderTest {

    @DisplayName("Http Header 응답값을 생성할 수 있다.")
    @Test
    void buildOutput() {
        // given
        HttpResponseHeader headers = new HttpResponseHeader();
        String expected = String.join("\r\n",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 10 "
        );

        // when
        headers.addHeader("Content-Type", "text/html;charset=utf-8");
        headers.addHeader("Content-Length", String.valueOf(10));
        String actual = headers.buildOutput();

        // then
        assertThat(actual).contains(expected);
    }
}
