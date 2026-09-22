package org.apache.coyote.http11.response;

import org.apache.coyote.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    @DisplayName("리다이렉트하면 302 상태와 Location 헤더를 응답한다.")
    void redirect() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        response.redirect("/index.html");

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html")
                .contains("Content-Length: 0");
    }

    @Test
    @DisplayName("쿠키를 추가하면 응답 헤더에 Set-Cookie를 포함한다.")
    void addCookie() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        response.addCookie("JSESSIONID=session-id");
        response.redirect("/index.html");

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("Set-Cookie: JSESSIONID=session-id");
    }

    @Test
    @DisplayName("정적 리소스를 응답하면 Content-Type과 Content-Length를 포함한다.")
    void sendStaticResource() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        response.sendStaticResource("/index.html");

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Content-Length:");
    }

    @Test
    @DisplayName("에러 응답을 보내면 해당 에러 페이지를 응답한다.")
    void sendError() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        response.sendError(HttpStatus.NOT_FOUND);

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 404 Not Found")
                .contains("Content-Type: text/html;charset=utf-8");
    }
}
