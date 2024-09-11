package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaderKey;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    @DisplayName("응답 헤더를 형식에 맞게 포멧팅하여 반환한다.")
    @Test
    void getHeaderResponse() {
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.add(HttpHeaderKey.SET_COOKIE, "cookie=very-tasty");
        responseHeaders.add(HttpHeaderKey.CONTENT_LENGTH, "80");

        String expected = "Set-Cookie: cookie=very-tasty \r\n" +
                          "Content-Length: 80";

        Assertions.assertThat(responseHeaders.getHeaderResponse()).isEqualTo(expected);
    }
}
