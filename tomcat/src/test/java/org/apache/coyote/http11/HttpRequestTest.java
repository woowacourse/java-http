package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void GET_메서드를_파싱한다() {
        final HttpRequest request = HttpRequest.from("GET /index.html HTTP/1.1");

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/index.html");
    }

    @Test
    void POST_메서드를_파싱한다() {
        final HttpRequest request = HttpRequest.from("POST /register HTTP/1.1");

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/register");
    }

    @Test
    void POST_요청의_헤더를_파싱한다() throws IOException {
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 65",
                "",
                "a".repeat(65));
        final HttpRequest request = requestFrom(httpRequest);

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/register");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getHeader("Content-Length")).isEqualTo("65");
    }

    @Test
    void POST_요청의_form_본문을_파싱한다() throws IOException {
        final String requestBody =
                "account=new-user&password=password&email=new-user%40example.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);
        final HttpRequest request = requestFrom(httpRequest);

        assertThat(request.getBodyParams())
                .containsEntry("account", "new-user")
                .containsEntry("password", "password")
                .containsEntry("email", "new-user@example.com");
    }

    @Test
    void POST_요청의_한글_form_본문을_바이트_길이만큼_읽는다() throws IOException {
        final String requestBody = "account=우테코";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);
        final HttpRequest request = requestFrom(httpRequest);

        assertThat(request.getBodyParams()).containsEntry("account", "우테코");
    }

    @Test
    void Cookie_헤더에서_JSESSIONID를_조회한다() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; JSESSIONID=session-id",
                "",
                "");
        final HttpRequest request = requestFrom(httpRequest);

        assertThat(request.getCookies().getValue("JSESSIONID")).contains("session-id");
    }

    @Test
    void Cookie_헤더가_없으면_JSESSIONID를_조회할_수_없다() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final HttpRequest request = requestFrom(httpRequest);

        assertThat(request.getCookies().getValue("JSESSIONID")).isEmpty();
    }

    private HttpRequest requestFrom(final String httpRequest) throws IOException {
        return HttpRequest.from(new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8)));
    }
}
