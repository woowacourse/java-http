package org.apache.coyote.http11.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.message.common.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("HTTP Response Message를 생성한다.")
    @Test
    void generateMessage() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP11;
        HttpStatus httpStatus = HttpStatus.OK;
        String body = "<div> hello, world </div>";

        HttpResponse httpResponse = new HttpResponse.Builder()
                .version(httpVersion)
                .status(httpStatus)
                .header("Content-Type", "text/html;charset=utf-8")
                .body(body)
                .build();

        // when
        String actual = httpResponse.generateMessage();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 25 ",
                "",
                "<div> hello, world </div>"
        );

        assertThat(actual).isEqualTo(expected);
    }
}
