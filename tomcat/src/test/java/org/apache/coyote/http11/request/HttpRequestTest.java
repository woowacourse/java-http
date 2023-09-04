package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("start-line 만 존재할 때 ( 쿼리 스트링 존재 )")
    @Test
    void parseHttpRequest_exist_start() {
        //given
        final String startLine = "GET /hello?name=split HTTP/1.1";
        final HttpRequestHeaders headers = new HttpRequestHeaders(Collections.emptyList());
        final String body = "";

        //when
        final HttpRequest request = new HttpRequest(startLine, headers, body);

        //then
        assertAll(
            () -> assertThat(request.getStartLine()).isEqualTo(startLine),
            () -> assertThat(request.getRequestTarget()).isEqualTo("/hello?name=split"),
            () -> assertThat(request.getRequestPath()).isEqualTo("/hello"),
            () -> assertThat(request.getQueryString()).isEqualTo("name=split"),
            () -> assertThat(request.getHeaders()).isEmpty(),
            () -> assertThat(request.getBody()).isEmpty()
        );
    }

    @DisplayName("start-line, header만 존재할 때 ( 쿼리 스트링 존재 X )")
    @Test
    void parseHttpRequest_exist_start_header() {
        //given
        final String startLine = "GET /hello HTTP/1.1";
        final HttpRequestHeaders headers = new HttpRequestHeaders(List.of("Host: www.example.com"));
        final String body = "";

        //when
        final HttpRequest request = new HttpRequest(startLine, headers, body);

        //then
        assertAll(
            () -> assertThat(request.getStartLine()).isEqualTo(startLine),
            () -> assertThat(request.getRequestTarget()).isEqualTo("/hello"),
            () -> assertThat(request.getRequestPath()).isEqualTo("/hello"),
            () -> assertThat(request.getHeaders()).hasSize(1),
            () -> assertThat(request.getHeaderValueIgnoringCase("host"))
                .isEqualTo("www.example.com"),
            () -> assertThat(request.getBody()).isEmpty()
        );
    }

    @DisplayName("start-line, header, body 가 존재할 때")
    @Test
    void parseHttpRequest_exist_all() {
        //given
        final String startLine = "GET /hello HTTP/1.1";
        final HttpRequestHeaders headers = new HttpRequestHeaders(List.of(
            "Host: www.example.com",
            "Accept: text/plain"
        ));
        final String body = "This is Body";

        //when
        final HttpRequest request = new HttpRequest(startLine, headers, body);

        //then
        assertAll(
            () -> assertThat(request.getStartLine()).isEqualTo(startLine),
            () -> assertThat(request.getRequestTarget()).isEqualTo("/hello"),
            () -> assertThat(request.getRequestPath()).isEqualTo("/hello"),
            () -> assertThat(request.getHeaders()).hasSize(2),
            () -> assertThat(request.getHeaderValueIgnoringCase("host"))
                .isEqualTo("www.example.com"),
            () -> assertThat(request.getHeaderValueIgnoringCase("accept"))
                .isEqualTo("text/plain"),
            () -> assertThat(request.getBody()).isEqualTo("This is Body")
        );
    }
}
