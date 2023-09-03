package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.ContentType.CSS;
import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.common.Status;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


class RequestHandlerTest {

    @DisplayName("/로 GET 요청을 보내면 Hello world!를 반환한다.")
    @Test
    void handleRoot() throws IOException {
        Response response = RequestHandler.handle(
                Request.from("get", "/", "localhost:8080", List.of("text/html"), "keep-alive", "")
        );

        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getContentType()).isEqualTo(HTML);
        assertThat(response.getBody()).isEqualTo("Hello world!");
    }

    @DisplayName("html 파일명을 자원으로 GET 요청을 보내면 resources/static 디렉토리 내의 동일한 파일을 찾아 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "/index.html", "/login.html", "/register.html", "/401.html"
    })
    void handleHTML(String URI) throws IOException {
        Response response = RequestHandler.handle(
                Request.from("get", URI, "localhost:8080", List.of("text/html"), "keep-alive", "")
        );

        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getContentType()).isEqualTo(HTML);
    }

    @DisplayName("css 파일명을 자원으로 GET 요청을 보내면 resources/static 디렉토리 내의 동일한 파일을 찾아 반환한다.")
    @Test
    void handleCSS() throws IOException {
        Response response = RequestHandler.handle(
                Request.from("get", "/css/styles.css", "localhost:8080", List.of("text/html"), "keep-alive", "")
        );

        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getContentType()).isEqualTo(CSS);
    }

    @DisplayName("resources/static 디렉토리 내에 존재하지 않는 파일명을 자원으로 GET 요청을 보내면 404 응답코드로 반환한다.")
    @Test
    void handleNotFound() throws IOException {
        Response response = RequestHandler.handle(
                Request.from(
                        "get", "/neverexist/not.css", "localhost:8080", List.of("text/css"), "keep-alive", "")
        );

        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND);
    }

}
