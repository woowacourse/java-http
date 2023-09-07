package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpRequestException;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceControllerTest {

    @Test
    @DisplayName("GET 메소드에 해당하는 로직을 처리해서 HttpResponse를 반환한다.")
    void getService() throws Exception {
        // given
        String startLine = "GET /index.html HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "Content-Type: text/html",
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        ResourceController resourceController = new ResourceController();

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expectedResponse = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        HttpResponse response = resourceController.service(request);
        String actualResponse = new String(response.convertToBytes());

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("POST 메소드 요청이 오면 예외를 반환한다.")
    void throwsWhenPostService() throws Exception {
        // given
        String startLine = "POST /index.html HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "Content-Type: text/html",
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        ResourceController resourceController = new ResourceController();

        // when & then
        assertThatThrownBy(() -> resourceController.service(request))
                .isInstanceOf(HttpRequestException.MethodNotAllowed.class)
                .hasMessage("허용되지 않는 HTTP Method 입니다.");
    }
}
