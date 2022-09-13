package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpMessageUtils;

@DisplayName("등록되지 않은 경로에 대해")
class ResourceControllerTest {

    private final Controller resourceController = new ResourceController();

    @DisplayName("GET 메서드의 요청은 해당 경로에 위치하는 파일을 찾아 200과 함께 응답한다.")
    @Test
    void get() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "",
                "");
        HttpResponse response = new HttpResponse();

        resourceController.service(HttpMessageUtils.toRequest(httpRequest), response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 200 OK \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/css;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 211991 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("css/styles.css"))
        );
    }

    @DisplayName("GET 메서드의 요청은 해당 경로에 위치하는 파일을 찾지 못하면 404와 404.html을 응답한다.")
    @Test
    void get_notFound() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /css/not-found.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        HttpResponse response = new HttpResponse();

        resourceController.service(HttpMessageUtils.toRequest(httpRequest), response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 404 Not Found \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 2426 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("404.html"))
        );
    }

    @DisplayName("POST 메서드의 요청은 상태코드 404과 404.html 파일을 응답한다")
    @Test
    void post() throws IOException {
        final String httpRequest = String.join("\r\n",
                "POST /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        HttpResponse response = new HttpResponse();

        resourceController.service(HttpMessageUtils.toRequest(httpRequest), response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 404 Not Found \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 2426 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("404.html"))
        );
    }
}
