package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void POST_요청의_메서드와_경로_헤더_본문을_읽는다() throws IOException {
        String body = "account=고래&password=비밀";
        String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);
        var input = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.readFrom(input);

        assertThat(request.hasMethod("POST")).isTrue();
        assertThat(request.getRequestTarget().hasPath("/login")).isTrue();
        assertThat(request.findHeader("Content-Length")).contains(
                String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        assertThat(request.findFormParameter("account")).contains("고래");
        assertThat(request.findFormParameter("password")).contains("비밀");
    }
}
