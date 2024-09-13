package org.was.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.http11.response.StatusLine;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @Test
    void uri_요청에_대해_응답을_설정() throws IOException {
        // given
        String uri = "/";
        RequestLine requestLine = new RequestLine("GET", uri, null, "HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html" );
        RequestHeader requestHeader = new RequestHeader(headers);

        HttpRequest request = new HttpRequest(requestLine, requestHeader, null);
        HttpResponse response = new HttpResponse();

        FrontController frontController = FrontController.getInstance();

        // when
        frontController.service(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static" + "/index.html");
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedContentLength = String.valueOf(expectedPage.getBytes().length);

        StatusLine statusLine = response.getStatusLine();
        ResponseHeader header = response.getHeader();
        ResponseBody body = response.getBody();

        assertAll(
                () -> assertThat(statusLine.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(statusLine.getStatusCode()).isEqualTo(200),
                () -> assertThat(statusLine.getStatusMessage()).isEqualTo("OK"),
                () -> assertThat(header.getHeaders()).hasSize(2),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Type", "text/html;charset=utf-8"),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Length", expectedContentLength),
                () -> assertThat(body.getContent()).isEqualTo(expectedPage)
        );
    }

    @Test
    void 처리할_수_없는_요청은_404_응답() throws IOException {
        // given
        String uri = "/nonExistentResource";
        RequestLine requestLine = new RequestLine("GET", uri, null, "HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html" );
        RequestHeader requestHeader = new RequestHeader(headers);

        HttpRequest request = new HttpRequest(requestLine, requestHeader, null);
        HttpResponse response = new HttpResponse();

        FrontController frontController = FrontController.getInstance();

        // when
        frontController.service(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static" + "/404.html");
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedContentLength = String.valueOf(expectedPage.getBytes().length);

        StatusLine statusLine = response.getStatusLine();
        ResponseHeader header = response.getHeader();
        ResponseBody body = response.getBody();

        assertAll(
                () -> assertThat(statusLine.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(statusLine.getStatusCode()).isEqualTo(404),
                () -> assertThat(statusLine.getStatusMessage()).isEqualTo("Not Found"),
                () -> assertThat(header.getHeaders()).hasSize(2),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Type", "text/html;charset=utf-8"),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Length", expectedContentLength),
                () -> assertThat(body.getContent()).isEqualTo(expectedPage)
        );
    }

    @Test
    void 지원하지_않는_메서드_요청은_405_응답() throws IOException {
        // given
        String method = "UPDATE";
        RequestLine requestLine = new RequestLine(method, "/login", null, "HTTP/1.1");
        RequestHeader requestHeader = new RequestHeader(new HashMap<>());

        HttpRequest request = new HttpRequest(requestLine, requestHeader, null);
        HttpResponse response = new HttpResponse();

        FrontController frontController = FrontController.getInstance();

        // when
        frontController.service(request, response);

        // then
        StatusLine statusLine = response.getStatusLine();

        assertAll(
                () -> assertThat(statusLine.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(statusLine.getStatusCode()).isEqualTo(405),
                () -> assertThat(statusLine.getStatusMessage()).isEqualTo("Method Not Allowed")
        );
    }
}
