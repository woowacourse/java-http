package org.apache.catalina.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.http11.response.StatusLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StaticResourceHandlerTest {

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/js/scripts.js", "/css/styles.css"})
    void 정적_자원_요청일_경우_true(String path) {
        // given
        RequestLine requestLine = new RequestLine("GET", path, null, "HTTP/1.1" );
        HttpRequest request = new HttpRequest(requestLine, null, null);

        StaticResourceHandler resourceHandler = StaticResourceHandler.getInstance();

        // when
        boolean actual = resourceHandler.canHandleRequest(request);

        // then
        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/login", "/register"})
    void 정적_자원이_아닐_경우_true(String path) {
        // given
        RequestLine requestLine = new RequestLine("GET", path, null, "HTTP/1.1" );
        HttpRequest request = new HttpRequest(requestLine, null, null);

        StaticResourceHandler resourceHandler = StaticResourceHandler.getInstance();

        // when
        boolean actual = resourceHandler.canHandleRequest(request);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void 정적_자원_요청에_대해_응답을_설정() throws IOException {
        // given
        String path = "/index.html";
        RequestLine requestLine = new RequestLine("GET", path, null, "HTTP/1.1" );
        RequestHeader requestHeader = new RequestHeader(new HashMap<>());

        HttpRequest request = new HttpRequest(requestLine, requestHeader, null);
        HttpResponse response = new HttpResponse();

        StaticResourceHandler resourceHandler = StaticResourceHandler.getInstance();

        // when
        resourceHandler.handle(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static" + path);
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        StatusLine statusLine = response.getStatusLine();
        ResponseHeader header = response.getHeader();
        ResponseBody body = response.getBody();

        assertAll(
                () -> assertThat(statusLine.getVersion()).isEqualTo("HTTP/1.1" ),
                () -> assertThat(statusLine.getStatusCode()).isEqualTo(200),
                () -> assertThat(statusLine.getStatusMessage()).isEqualTo("OK" ),
                () -> assertThat(header.getHeaders()).hasSize(2),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Type", "text/html;charset=utf-8" ),
                () -> assertThat(header.getHeaders()).containsEntry("Content-Length", "5038" ),
                () -> assertThat(body.getContent()).isEqualTo(expectedPage)
        );
    }

    @Test
    void 존재하지_않는_자원에_대한_요청일_경우_404_응답() throws IOException {
        // given
        String path = "non existent resource";
        RequestLine requestLine = new RequestLine("GET", path, null, "HTTP/1.1" );
        RequestHeader requestHeader = new RequestHeader(new HashMap<>());

        HttpRequest request = new HttpRequest(requestLine, requestHeader, null);
        HttpResponse response = new HttpResponse();

        StaticResourceHandler resourceHandler = StaticResourceHandler.getInstance();

        // when
        resourceHandler.handle(request, response);

        // then
        URL resource = getClass().getClassLoader().getResource("static" + "/404.html" );
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        StatusLine statusLine = response.getStatusLine();
        ResponseHeader header = response.getHeader();
        ResponseBody body = response.getBody();

        assertAll(
                () -> assertThat(statusLine.getVersion()).isEqualTo("HTTP/1.1" ),
                () -> assertThat(statusLine.getStatusCode()).isEqualTo(404),
                () -> assertThat(statusLine.getStatusMessage()).isEqualTo("Not Found" ),
                () -> assertThat(header).isNull(),
                () -> assertThat(body.getContent()).isEqualTo(expectedPage)
        );
    }
}
