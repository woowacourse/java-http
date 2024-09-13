package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11ResponseTest {

    @DisplayName("리다이렉트하면 Location 헤더가 추가된다.")
    @Test
    void sendRedirect() {
        Http11Response response = Http11Response.create();
        response.sendRedirect("/login");

        assertThat(response.toString()).contains("Location: /login");
    }

    @DisplayName("정적리소스 파일을 바디에 추가한다.")
    @Test
    void addStaticBody() throws IOException {
        Http11Response response = Http11Response.create();
        response.addStaticBody("/index.html");

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assertThat(response.toString()).contains(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    @DisplayName("쿠키를 key, value 형태로 추가한다.")
    @Test
    void addCookie() {
        Http11Response response = Http11Response.create();
        response.addCookie("a", "b");

        assertThat(response.toString()).contains("Cookie: a=b");
    }

    @DisplayName("ContentType 헤더를 추가한다.")
    @Test
    void addContentType() {
        Http11Response response = Http11Response.create();
        response.addContentType("text/html");

        assertThat(response.toString()).contains("Content-Type: text/html");
    }

    @DisplayName("바디를 추가한다.")
    @Test
    void addBody() {
        Http11Response response = Http11Response.create();
        response.addBody("Hello");

        assertThat(response.toString()).contains("Hello");
    }
}
