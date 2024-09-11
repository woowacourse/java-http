package org.apache.coyote.http11.response.header;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import org.apache.coyote.http11.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    @DisplayName("응답 헤더 메시지를 작성한다.")
    @Test
    void toMessageTest() {
        LinkedHashMap<HttpHeader, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeader.CONTENT_TYPE, ContentType.HTML.value());
        headers.put(HttpHeader.CONTENT_LENGTH, "100");

        ResponseHeaders responseHeaders = new ResponseHeaders(headers);
        String message = responseHeaders.toMessage();

        String expected = String.join("\r\n",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 100",
                "");

        assertThat(message).isEqualTo(expected);
    }
}
