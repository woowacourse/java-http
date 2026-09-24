package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 요청에서_이름으로_쿠키_값을_찾는다() throws IOException {
        String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Cookie: yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");
        var input = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.readFrom(input);

        assertThat(request.findCookie("JSESSIONID"))
                .contains("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

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

        assertThat(request.matches("POST", "/login")).isTrue();
        assertThat(request.matches("GET", "/login")).isFalse();
        assertThat(request.matches("POST", "/register")).isFalse();
        assertThat(request.findHeader("Content-Length")).contains(
                String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        assertThat(request.findFormParameter("account")).contains("고래");
        assertThat(request.findFormParameter("password")).contains("비밀");
    }
}
