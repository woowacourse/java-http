package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpMessageUtils;

@DisplayName("/ & /index 경로에 대해")
class HomeControllerTest {

    private final Controller homeController = new HomeController();

    @DisplayName("GET 메서드의 요청은 상태코드 200과 index.html 파일을 응답한다")
    @Test
    void get() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        HttpResponse response = new HttpResponse();

        homeController.service(HttpMessageUtils.toRequest(httpRequest), response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 200 OK \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 5564 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("index.html"))
        );
    }

    @DisplayName("POST 메서드의 요청은 상태코드 404과 404.html 파일을 응답한다")
    @Test
    void post() throws IOException {
        final String httpRequest = String.join("\r\n",
                "POST /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        HttpResponse response = new HttpResponse();

        homeController.service(HttpMessageUtils.toRequest(httpRequest), response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 404 Not Found \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 2426 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("404.html"))
        );
    }
}
