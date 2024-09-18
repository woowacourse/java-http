package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestTest {

    @DisplayName("InputStream으로 Http11Request를 만든다.")
    @Test
    void from() throws IOException {
        String header = "GET / HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: 30\r\n";
        String emptyLine = "\r\n";
        String body = "account=gugu&password=password";
        String requestString = header + emptyLine + body;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                requestString.getBytes(StandardCharsets.UTF_8));

        Http11Request request = Http11Request.from(byteArrayInputStream);

        assertThat(request.getHttp11RequestHeader().getContentLength()).isEqualTo(30);
        assertThat(request.getHttp11RequestBody().getBody()).isEqualTo(body);
        assertThat(request.getRequestLine().getHttpMethod()).isEqualTo(HttpMethod.GET);
    }
}
