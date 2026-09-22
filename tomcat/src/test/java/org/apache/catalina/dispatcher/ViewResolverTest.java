package org.apache.catalina.dispatcher;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.line.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ViewResolverTest {

    private final ViewResolver viewResolver = new ViewResolver();

    @Test
    @DisplayName("뷰 이름에 해당하는 정적 리소스를 바디에 담는다.")
    void resolveResource() throws IOException {
        // given
        HttpResponse response = new HttpResponse();

        // when
        viewResolver.resolve("/index.html", response);

        // then
        String content = readResource("static/index.html");
        assertThat(write(response)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + content.getBytes(StandardCharsets.UTF_8).length,
                "",
                content));
    }

    @Test
    @DisplayName("확장자에 맞는 Content-Type을 설정한다.")
    void resolveContentType() throws IOException {
        // given
        HttpResponse response = new HttpResponse();

        // when
        viewResolver.resolve("/css/styles.css", response);

        // then
        assertThat(write(response)).contains("Content-Type: text/css;charset=utf-8");
    }

    @Test
    @DisplayName("redirect: 접두사가 붙으면 바디 없이 리다이렉트한다.")
    void resolveRedirect() throws IOException {
        // given
        HttpResponse response = new HttpResponse();

        // when
        viewResolver.resolve("redirect:/index.html", response);

        // then
        assertThat(write(response)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 302 FOUND",
                "Location: /index.html",
                "Content-Length: 0",
                ""));
    }

    @Test
    @DisplayName("리소스가 없으면 404.html을 404로 응답한다.")
    void resolveMissingResource() throws IOException {
        // given
        HttpResponse response = new HttpResponse();

        // when
        viewResolver.resolve("/not-found.html", response);

        // then
        String content = readResource("static/404.html");
        assertThat(write(response))
                .startsWith("HTTP/1.1 404 Not Found\r\n")
                .endsWith(content);
    }

    @Test
    @DisplayName("미리 설정된 상태를 덮어쓰지 않는다.")
    void keepStatus() throws IOException {
        // given
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED);

        // when
        viewResolver.resolve("/401.html", response);

        // then
        assertThat(write(response)).startsWith("HTTP/1.1 401 Unauthorized\r\n");
    }

    private String readResource(String name) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(name)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String write(HttpResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.write(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

}
