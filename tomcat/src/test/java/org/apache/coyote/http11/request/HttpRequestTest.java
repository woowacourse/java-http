package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private HttpRequest httpRequest;

    @DisplayName("응답에서 requestUri와 헤더를 얻는다.")
    @Test
    void getRequestUriAndHeaders() throws IOException {
        //given
        final String string = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());
        httpRequest = HttpRequestFactory.parse(inputStream);

        //when,then
        assert httpRequest != null;
        assertThat(httpRequest.getRequestLine()).isEqualTo("/index.html");
        final RequestHeaders requestHeaders = httpRequest.getRequestHeaders();
        assertThat(requestHeaders.getRequestHeaders()).hasSize(2);
        assertThat(requestHeaders.getRequestHeaders().get("Host")).isEqualTo("localhost:8080");
        assertThat(requestHeaders.getRequestHeaders().get("Connection")).isEqualTo("keep-alive");
    }


}
