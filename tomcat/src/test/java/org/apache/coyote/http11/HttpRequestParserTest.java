package org.apache.coyote.http11;

import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestParserTest {

    @Test
    @DisplayName("InpuStream 에서 HTTP Request 요청을 만든다.")
    void parse_http_request_to_inpustream() throws IOException {

        final byte[] requestBytes =
                String.join("\r\n",
                                "GET /index.html HTTP/1.1 ",
                                "Host: localhost:8080 ",
                                "Connection: keep-alive ",
                                "",
                                "")
                        .getBytes();

        final HttpRequest request = HttpRequestParser.parse(new ByteArrayInputStream(requestBytes));
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
        assertThat(request.getHeaders().get("Connection")).isEqualTo("keep-alive ");
        assertThat(request.getHeaders().get("Host")).isEqualTo("localhost:8080 ");
    }
    @Test
    @DisplayName("HTTP RequestLine 이 잘못되면 예외를 발생한다.")
    void throw_exception_when_request_line_informat(){
        final byte[] requestBytes =
                String.join("\r\n",
                                "GET HTTP/1.1 ",
                                "Host: localhost:8080 ",
                                "Connection: keep-alive ",
                                "",
                                "")
                        .getBytes();

        assertThatThrownBy(()->HttpRequestParser.parse(new ByteArrayInputStream(requestBytes)))
                .isInstanceOf(IllegalArgumentException.class);

    }

}
