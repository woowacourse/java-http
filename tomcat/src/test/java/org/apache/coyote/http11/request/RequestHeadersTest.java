package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.StringReader;
import nextstep.jwp.exception.InvalidHttpRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @DisplayName("header 가 정상적으로 들어오지 않을 시, 파싱 에러가 난다.")
    @Test
    void parseHeaders() {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("Content: Error: type"));
        assertThatThrownBy(() -> RequestHeaders.from(bufferedReader))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Cookie 가 없을 때 사용하려고 하면 에러가 난다.")
    @Test
    void findCookies() {
        // given
        var expected = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        // when & then
        final HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(expected)));
        assertThatThrownBy(() -> request.getCookie())
                .isInstanceOf(InvalidHttpRequestException.class);
    }
}

