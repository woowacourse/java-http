package org.apache.coyote.http11.message.request;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestReaderTest {

    @Test
    @DisplayName("HttpRequest 객체를 생성한다.")
    void readTest() throws IOException {
        // given
        String body = "key1=value1&key2=value2";
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequestReader reader = new HttpRequestReader(inputStream);

        // when
        HttpRequest request = reader.read();

        // then
        assertAll(
                () -> assertEquals(HttpMethod.GET, request.getMethod()),
                () -> assertEquals("/index.html", request.getUrlPath()),
                () -> assertEquals("value1", request.getFormParameter("key1")),
                () -> assertEquals("value2", request.getFormParameter("key2"))
        );
    }
}
