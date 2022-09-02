package org.apache.coyote.http11.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.common.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseMessageTest {

    @DisplayName("HTTP Response Message를 생성한다.")
    @Test
    void generateMessage() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP11;
        HttpStatus httpStatus = HttpStatus.OK;
        HttpHeaders httpHeaders = new HttpHeaders(Map.of("Content-Type", "text/html;charset=utf-8"));
        String body = "<div> hello, world </div>";

        ResponseMessage responseMessage = new ResponseMessage(httpVersion, httpStatus, httpHeaders, body);

        // when
        String actual = responseMessage.generateMessage();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "",
                "<div> hello, world </div>"
        );

        assertThat(actual).isEqualTo(expected);
    }
}
