package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceHandlerTest extends ControllerTest {

    private final StaticResourceHandler sut = new StaticResourceHandler();

    @Test
    @DisplayName("html 파일 파싱 성공")
    void handleHtml() throws Exception {
        var request = httpRequest(String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "",
                ""));

        var response = new HttpResponse();
        sut.service(request, response);

        assertThat(response.asString())
                .contains("HTTP/1.1 200 OK ")
                .contains("Content-Type: text/html ");
    }

    @Test
    @DisplayName("css 파일 파싱 성공")
    void handleCss() throws Exception {
        var request = httpRequest(String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "",
                ""));

        var response = new HttpResponse();
        sut.service(request, response);

        assertThat(response.asString())
                .contains("HTTP/1.1 200 OK ")
                .contains("Content-Type: text/css ");
    }

    @Test
    @DisplayName("javascript 파일 파싱 성공")
    void handleJs() throws Exception {
        var request = httpRequest(String.join("\r\n",
                "GET /assets/chart-area.js HTTP/1.1 ",
                "",
                ""));

        var response = new HttpResponse();
        sut.service(request, response);

        assertThat(response.asString())
                .contains("HTTP/1.1 200 OK ")
                .contains("Content-Type: application/javascript ");
    }
}
