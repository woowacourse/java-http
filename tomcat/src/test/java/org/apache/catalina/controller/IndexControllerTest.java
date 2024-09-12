package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("인덱스 컨트롤러")
class IndexControllerTest {

    private IndexController indexController;

    @BeforeEach
    void setUp() {
        this.indexController = new IndexController();
    }

    @DisplayName("인덱스 컨트롤러는 get 요청 시 Index 페이지를 응답한다.")
    @Test
    void doGet() throws IOException {
        // given
        List<String> headers = List.of(
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );
        HttpRequest request = HttpRequest.of(headers, "");
        HttpResponse response = new HttpResponse();
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = Files.readString(new File(resource.getFile()).toPath());

        // when
        indexController.doGet(request, response);

        // then
        assertThat(response).extracting("body")
                .isEqualTo(expected);
    }

    @DisplayName("인덱스 컨트롤러는 post 요청 시 지원하지 않는다는 예외를 발생한다.")
    @Test
    void doPost() {
        // given
        String body = "test=test";
        List<String> headers = List.of(
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded "
        );
        HttpRequest request = HttpRequest.of(headers, body);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(() -> indexController.doPost(request, response))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("POST는 지원하지 않습니다.");
    }
}
