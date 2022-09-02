package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestHeaderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("헤데어세 요청한 자원을 응답 값으로 잘 반환하는지 테스트")
    @Test
    void getResponse() throws IOException {
        // given
        final String string = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());
        HttpRequestHeader httpRequestHeader = HttpRequestHeaderFactory.parse(inputStream);

        // when
        HttpResponse httpResponse = new HttpResponse(httpRequestHeader);
        String actual = httpResponse.getResponse();

        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
