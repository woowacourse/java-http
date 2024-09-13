package com.techcourse.view;

import static org.apache.coyote.http11.Http11Processor.HTTP_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @Test
    @DisplayName("기본 경로에 대한 요청은 기본 메세지를 보여준다.")
    void resoleRootPath() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, "/", null, HTTP_VERSION),
                null, null
        );
        HttpResponse httpResponse = new HttpResponse();
        ViewResolver viewResolver = new ViewResolver();

        // when
        HttpResponse response = viewResolver.resolve(httpRequest, httpResponse);

        // then
        assertEquals(response.getBody(), "Hello world!");
    }

    @Test
    @DisplayName("특정 리소스에 대한 요청은 그 리소스 내용을 응답한다.")
    void resolveStaticResource() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, "/login.html", null, HTTP_VERSION),
                null, null
        );
        HttpResponse httpResponse = new HttpResponse();
        ViewResolver viewResolver = new ViewResolver();

        // when
        HttpResponse response = viewResolver.resolve(httpRequest, httpResponse);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expectedHeader = """
                HTTP/1.1 200 OK
                Content-Type: text/html;charset=utf-8
                Content-Length:""";
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response.toString()).startsWith(expectedHeader);
        assertThat(response.toString()).contains(expectedBody);
    }

    @Test
    @DisplayName("리소스에 파일 확장자를 붙이지 않는다면, html 파일로 인식한다.")
    void resolveStaticResourceWithoutExtension() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, "/login", null, HTTP_VERSION),
                null, null
        );
        HttpResponse httpResponse = new HttpResponse();
        ViewResolver viewResolver = new ViewResolver();

        // when
        HttpResponse response = viewResolver.resolve(httpRequest, httpResponse);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expectedHeader = """
                HTTP/1.1 200 OK
                Content-Type: text/html;charset=utf-8
                Content-Length:""";
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response.toString()).startsWith(expectedHeader);
        assertThat(response.toString()).contains(expectedBody);
    }

    @Test
    @DisplayName("존재하지 않는 리소스에 대한 요청은 null을 반환한다.")
    void resolveNonExistResource() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(
                new HttpRequestLine(HttpMethod.GET, "/non-exist.html", null, HTTP_VERSION),
                null, null
        );
        HttpResponse httpResponse = new HttpResponse();
        ViewResolver viewResolver = new ViewResolver();

        // when
        HttpResponse response = viewResolver.resolve(httpRequest, httpResponse);

        // then
        assertThat(response).isNull();
    }
}
