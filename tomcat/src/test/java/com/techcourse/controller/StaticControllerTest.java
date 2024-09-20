package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.coyote.http11.message.parser.HttpRequestParser;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticControllerTest {

    @DisplayName("GET /index.html -> 200 인덱스 페이지")
    @Test
    void getIndexPage() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "");

        HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
        HttpResponse httpResponse = new HttpResponse();

        // when
        new StaticController().doGet(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.findViewUri()).isPresent()
                .hasValue("/index.html");
    }

    @DisplayName("GET /js/scripts.js -> 200 js 페이지")
    @Test
    void getJsPage() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/javascript;charset=utf-8 ",
                "Connection: keep-alive",
                "",
                "");

        HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
        HttpResponse httpResponse = new HttpResponse();

        // when
        new StaticController().doGet(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.findViewUri()).isPresent()
                .hasValue("/js/scripts.js");
    }

    @DisplayName("GET /css/styles.css -> 200 css 페이지")
    @Test
    void getCssPage() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive",
                "",
                "");

        HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
        HttpResponse httpResponse = new HttpResponse();

        // when
        new StaticController().doGet(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.findViewUri()).isPresent()
                .hasValue("/css/styles.css");
    }
}
