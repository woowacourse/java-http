package org.apache.coyote.http11.domain.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceControllerTest {

    @Test
    @DisplayName("인덱스 페이지 요청을 처리한다.")
    void serviceIndexPage() throws IOException {
        ResourceController resourceController = new ResourceController();
        String requestLine = "GET /index.html HTTP/1.1";
        List<String> headerLines = List.of("Host: localhost:8080", "Connection: keep-alive");
        HttpRequest httpRequest = new HttpRequest(requestLine, headerLines);

        HttpResponse httpResponse = resourceController.service(httpRequest);

        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("static 파일 요청을 처리한다.")
    void serviceStaticResource() throws IOException {
        ResourceController resourceController = new ResourceController();
        String requestLine = "GET /static/login.html HTTP/1.1";
        List<String> headerLines = List.of("Host: localhost:8080", "Connection: keep-alive");
        HttpRequest httpRequest = new HttpRequest(requestLine, headerLines);

        HttpResponse httpResponse = resourceController.service(httpRequest);

        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
