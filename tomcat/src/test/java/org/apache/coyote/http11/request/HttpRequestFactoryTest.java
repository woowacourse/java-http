package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestFactoryTest {

    @DisplayName("받은 InputStream을 파싱해서 HttpRequestHeader를 반환한다.")
    @Test
    void parse() throws IOException {
        // given
        final String string = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(string.getBytes());

        // when
        HttpRequest httpRequestHeader = HttpRequestFactory.parse(inputStream);

        //then
        assert httpRequestHeader != null;
        assertThat(httpRequestHeader.getRequestLine().getRequestLine()).isEqualTo("GET /index.html HTTP/1.1 ");
        assertThat(httpRequestHeader.getRequestHeaders()).isNotNull();
    }
}
