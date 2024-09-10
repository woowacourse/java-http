package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @DisplayName("인덱스 컨트롤러는 post 요청 시 지원하지 않는다는 예외를 발생한다.")
    @Test
    void doPost() {
        String body = "test=test";
        List<String> headers = List.of(
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded "
        );
        HttpRequest httpRequest = HttpRequest.of(headers, body);
        HttpResponse httpResponse = new HttpResponse();

        assertThatThrownBy(() -> indexController.doPost(httpRequest, httpResponse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("POST는 지원하지 않습니다.");
    }
}
