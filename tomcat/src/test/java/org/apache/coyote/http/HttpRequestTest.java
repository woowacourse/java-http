package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("Http request 파싱 성공")
    void httpRequestHeaders() throws IOException {
        // given
        var requestLine = "POST /path HTTP/1.1 ";
        var headers = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ");
        var body = "Hello world!";
        var request = String.join("\r\n",
                requestLine,
                headers,
                "",
                body);

        // when
        var sut = new HttpRequest(new ByteArrayInputStream(request.getBytes()));

        // then
        assertThat(sut.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(sut.getPath()).isEqualTo("/path");
        assertThat(sut.getHttpVersion()).isEqualTo(HttpVersion.HTTP11);
        assertThat(sut.getAccept()).isEqualTo("*/* ");
        assertThat(sut.getBody().asString()).isEqualTo(body);
    }
}
