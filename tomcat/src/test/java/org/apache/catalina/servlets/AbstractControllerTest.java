package org.apache.catalina.servlets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.ResourceLocator;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    private AbstractController controller;
    HttpResponse response;


    @BeforeEach
    void setUp() {
        controller = new AbstractController(new ResourceLocator("/static")) {
            @Override
            public boolean isProcessable(HttpRequest request) {
                return false;
            }
        };
        response = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("입력된 경로에 리소스가 존재하면 response의 상태코드를 200, Content-Type을 text/html로 변경한다")
    void return200Response() {
        controller.doHtmlResponse(response, "/index.html");

        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getHeader("Content-Type")).isEqualTo("text/html")
        );
    }

    @Test
    @DisplayName("입력된 경로에 리소스가 존재하지 않으면 response의 상태코드를 404, Content-Type을 text/html로 변경한다")
    void return404Response() {
        controller.doHtmlResponse(response, "/unknown.html");

        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getHeader("Content-Type")).isEqualTo("text/html")
        );
    }
}
