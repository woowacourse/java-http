package com.techcourse.framework.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.protocol.request.HttpRequest;
import org.apache.coyote.http11.protocol.request.RequestBody;
import org.apache.coyote.http11.protocol.request.RequestHeaders;
import org.apache.coyote.http11.protocol.request.RequestLine;
import org.apache.coyote.http11.protocol.response.HttpResponse;
import org.apache.coyote.http11.protocol.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceControllerTest {

    @Test
    @DisplayName("인덱스 페이지 요청을 처리한다.")
    void serviceIndexPage() throws IOException {
        ResourceController resourceController = new ResourceController();
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Host: localhost:8080", "Connection: keep-alive"));
        RequestBody requestBody = new RequestBody("test body");
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse response = HttpResponse.status(HttpStatus.OK).build();

        resourceController.service(request, response);

        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("static 파일 요청을 처리한다.")
    void serviceStaticResource() throws IOException {
        ResourceController resourceController = new ResourceController();
        RequestLine requestLine = new RequestLine("GET /static/login.html HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Host: localhost:8080", "Connection: keep-alive"));
        RequestBody requestBody = new RequestBody("test body");
        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse response = HttpResponse.status(HttpStatus.OK).build();

        resourceController.service(httpRequest, response);

        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
