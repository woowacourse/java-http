package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    private HttpRequestHeader httpRequestHeader;

    @DisplayName("응답 헤더에서 requestUri를 얻는다.")
    @Test
    void getRequestUri() throws IOException {
        //given
        final String string = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());
        httpRequestHeader = HttpRequestHeaderFactory.parse(inputStream);

        //when,then
        assert httpRequestHeader != null;
        assertThat(httpRequestHeader.getRequestUri())
                .isEqualTo("/index.html");
    }
}
