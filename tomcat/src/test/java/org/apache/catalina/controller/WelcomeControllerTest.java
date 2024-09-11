package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("웰컴 컨트롤러")
class WelcomeControllerTest {

    private WelcomeController welcomeController;

    @BeforeEach
    void setUp() {
        this.welcomeController = new WelcomeController();
    }

    @DisplayName("웰컴 컨트롤러는 루트 페이지 요청 시 Hello world를 반환한다.")
    @Test
    void doGet() throws IOException {
        // given
        List<String> headers = List.of(
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );
        HttpRequest request = HttpRequest.of(headers, "");
        HttpResponse response = new HttpResponse();
        String expected = "Hello world!";

        // when
        welcomeController.doGet(request, response);

        // then
        assertThat(response).extracting("body")
                .isEqualTo(expected);
    }

    @DisplayName("웰컴 컨트롤러는 루트로 POSt 요청 시 예외를 발생 시킨다.")
    @Test
    void doPost() {
        // given
        String body = "test=test";
        List<String> headers = List.of(
                "POST / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded "
        );
        HttpRequest request = HttpRequest.of(headers, body);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(() -> welcomeController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("POST는 지원하지 않습니다.");
    }
}
