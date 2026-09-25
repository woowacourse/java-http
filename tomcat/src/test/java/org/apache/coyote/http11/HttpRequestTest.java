package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void readsRequestBodyUsingContentLengthInBytes() {
        // given
        final String body = "account=조상준&password=비밀번호";
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);

        // when
        final HttpRequest httpRequest = new HttpRequest(
                new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8))
        );

        // then
        assertThat(httpRequest.getBody()).isEqualTo(body);
        assertThat(httpRequest.getParameter("account")).isEqualTo("조상준");
        assertThat(httpRequest.getParameter("password")).isEqualTo("비밀번호");
    }
}
