package nextstep.jwp.controller;

import static org.apache.coyote.http11.HandlerMapping.LOGIN;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.HandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginControllerTest {

    @DisplayName("로그인에 성공하면 302, index.html을 응답한다.")
    @Test
    void login_success() throws Exception {
        // given
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String indexPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String requestValue = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: text/html ",
                "",
                "");
        HttpRequest request = HttpRequest.from(toBufferedReader(requestValue));

        // when
        HttpResponse response = HandlerMapping.getMethodHandler(request)
                .service();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader("Location")).isEqualTo("/index.html");
    }

    @DisplayName("로그인에 실패하면 401, 401.html을 응답한다.")
    @Test
    void login_fail() throws Exception {
        // given
        URL resource = getClass().getClassLoader().getResource("static/401.html");
        String unauthorizedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String requestValue = String.join("\r\n",
                "GET /login?account=gugu&password=invalidPassword HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: text/html ",
                "",
                "");
        HttpRequest request = HttpRequest.from(toBufferedReader(requestValue));

        // when
        HttpResponse response = HandlerMapping.getMethodHandler(request)
                .service();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader("Location")).isEqualTo("/401.html");
    }

    private BufferedReader toBufferedReader(String request) {
        return new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
    }
}
