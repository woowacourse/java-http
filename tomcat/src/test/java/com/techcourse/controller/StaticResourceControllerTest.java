package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.exception.UnsupportedMethodException;

class StaticResourceControllerTest {
    private StaticResourceController staticResourceController;

    @BeforeEach
    void setUp() {
        staticResourceController = StaticResourceController.getInstance();
    }

    @DisplayName("올바른 리소스에 대해 200 응답을 반환한다.")
    @Test
    void findResource() throws Exception {
        // given
        RequestLine requestLine = RequestLine.from("GET /css/styles.css HTTP/1.1");
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        staticResourceController.service(httpRequest, httpResponse);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expectedResponseLine = "HTTP/1.1 200 OK \r\n";
        String expectedContentLength = "Content-Length: 211991 \r\n";
        String expectedContentType = "Content-Type: text/css \r\n";
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedContentLength,
                expectedContentType,
                expectedResponseBody
        );
    }

    @DisplayName("GET 외의 메서드로 리소스에 대한 요청이 오면 예외가 발생한다.")
    @Test
    void cannotFindResourceWrongMethod() throws Exception {
        // given
        RequestLine requestLine = RequestLine.from("POST /css/styles.css HTTP/1.1");
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when & then
        assertThatThrownBy(() -> staticResourceController.service(httpRequest, httpResponse))
                .isInstanceOf(UnsupportedMethodException.class);
    }
}
