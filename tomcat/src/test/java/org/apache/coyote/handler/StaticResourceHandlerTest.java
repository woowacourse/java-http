package org.apache.coyote.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.header.HttpHeaderName;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceHandlerTest {

    @Test
    @DisplayName("정적 리소스 처리: 기본적으로 /static 경로에 있는 리소스를 반환")
    void handle() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/404.html");
        final String responseBody = Files.readString(Path.of(resourceURL.getPath()));

        final RequestLine requestLine = new RequestLine("GET", "/404.html", "HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine);
        final HttpResponse httpResponse = StaticResourceHandler.getInstance().handle(httpRequest);

        final HttpResponse actual = HttpResponse.builder()
                .addHeader(HttpHeaderName.CONTENT_TYPE, "text/html")
                .body(responseBody)
                .okBuild();
        assertEquals(httpResponse, actual);
    }

    @Test
    @DisplayName("정적 리소스 처리: 확장자에 따라 다른 경로에 있는 리소스를 반환")
    void handle_When_Different_Extension_Resource() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/css/styles.css");
        final String responseBody = Files.readString(Path.of(resourceURL.getPath()));

        final RequestLine requestLine = new RequestLine("GET", "/css/styles.css", "HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine);
        final HttpResponse httpResponse = StaticResourceHandler.getInstance().handle(httpRequest);

        final HttpResponse actual = HttpResponse.builder()
                .body(responseBody)
                .addHeader(HttpHeaderName.CONTENT_TYPE, "text/css")
                .okBuild();
        assertEquals(httpResponse, actual);
    }

    @Test
    @DisplayName("정적 리소스 처리: 확장자가 없는 경우, /static 경로에 있는 html 리소스를 반환")
    void handle_When_Extension_NotExist() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/404.html");
        final String responseBody = Files.readString(Path.of(resourceURL.getPath()));

        final HttpRequest httpRequest = new HttpRequest(new RequestLine("GET", "/404.html", "HTTP/1.1"));
        final HttpResponse httpResponse = StaticResourceHandler.getInstance().handle(httpRequest);

        final HttpResponse actual = HttpResponse.builder()
                .addHeader(HttpHeaderName.CONTENT_TYPE, "text/html")
                .body(responseBody)
                .okBuild();
        assertEquals(httpResponse, actual);
    }
}
