package org.apache.coyote.http11.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.support.HttpHeader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStartLine;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.LinkedHashMap;

class RequestHandlerTest {

    @DisplayName("로그인 요청을 Query String과 함께하면 로그인 성공 응답을 반환한다.")
    @Test
    void handle_returnsLoginControllerResponse() throws IOException {
        // given
        final HttpStartLine httpStartLine = HttpStartLine.from(
                new String[]{"GET", "/login?account=gugu&password=password", "HTTP/1.1"}
        );
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.HOST, "localhost:8080");
        httpHeaders.put(HttpHeader.CONNECTION, "keep-alive");
        httpHeaders.put(HttpHeader.ACCEPT, "text/html");
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, httpHeaders, "");

        // when
        final HttpResponse httpResponse = new RequestHandler().handle(httpRequest);

        // then
        final String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html \r\n" +
                "Content-Type: text/html \r\n" +
                "Content-Length: 0 \r\n" +
                "\r\n";
        assertThat(httpResponse.format()).isEqualTo(expected);

    }

    @DisplayName("register로 GET 요청을 하면 register 페이지를 응답한다.")
    @Test
    void handle_returnsRegisterControllerResponse() throws IOException {
        // given
        final HttpStartLine httpStartLine = HttpStartLine.from(
                new String[]{"GET", "/register", "HTTP/1.1"}
        );
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.HOST, "localhost:8080");
        httpHeaders.put(HttpHeader.CONNECTION, "keep-alive");
        httpHeaders.put(HttpHeader.ACCEPT, "text/html");
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, httpHeaders, "");

        // when
        final HttpResponse httpResponse = new RequestHandler().handle(httpRequest);

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html \r\n" +
                "Content-Length: 4319 \r\n" +
                "\r\n";
        assertThat(httpResponse.format()).startsWith(expected);
    }

    @DisplayName("기본 uri인 / 로 GET 요청을 하면 Default Response로 응답한다.")
    @Test
    void handle_returnsDefaultResponse() throws IOException {
        // given
        final HttpStartLine httpStartLine = HttpStartLine.from(
                new String[]{"GET", "/", "HTTP/1.1"}
        );
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.HOST, "localhost:8080");
        httpHeaders.put(HttpHeader.CONNECTION, "keep-alive");
        httpHeaders.put(HttpHeader.ACCEPT, "text/html");
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, httpHeaders, "");

        // when
        final HttpResponse httpResponse = new RequestHandler().handle(httpRequest);

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html \r\n" +
                "Content-Length: 12 \r\n" +
                "\r\n";
        assertThat(httpResponse.format()).startsWith(expected);
    }
}
