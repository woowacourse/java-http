package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.header.Headers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("start-line 만 존재할 때")
    @Test
    void parseHttpRequest_exist_start() {
        //given
        final String startLine = "GET /hello?name=split HTTP/1.1";

        //when
        final HttpRequest request = HttpRequest.requestLine(startLine);

        //then
        assertAll(
            () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
            () -> assertThat(request.getPath()).isEqualTo("/hello"),
            () -> assertThat(request.getProtocolVersion()).isEqualTo(HttpVersion.HTTP_1_1),
            () -> assertThat(request.getQueryParameters()).isEqualTo(Map.of("name", "split")),
            () -> assertThat(request.getHeaders().getAllHeaders()).isEmpty(),
            () -> assertThat(request.getBody()).isEmpty()
        );
    }

    @DisplayName("start-line, header만 존재할 때")
    @Test
    void parseHttpRequest_exist_start_header() {
        //given
        final String startLine = "GET /hello HTTP/1.1";
        final Headers headers = new Headers(List.of("Host: www.example.com"));

        //when
        final HttpRequest request = HttpRequest.builder(startLine)
            .headers(headers)
            .build();

        //then
        assertAll(
            () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
            () -> assertThat(request.getPath()).isEqualTo("/hello"),
            () -> assertThat(request.getProtocolVersion()).isEqualTo(HttpVersion.HTTP_1_1),
            () -> assertThat(request.getHeaders().getAllHeaders()).hasSize(1),
            () -> assertThat(request.getHeader("Host"))
                .isEqualTo("www.example.com"),
            () -> assertThat(request.getBody()).isEmpty()
        );
    }

    @DisplayName("start-line, header, body 가 존재할 때")
    @Test
    void parseHttpRequest_exist_all() {
        //given
        final String startLine = "GET /hello HTTP/1.1";
        final Headers headers = new Headers(List.of(
            "Host: www.example.com",
            "Accept: text/plain"
        ));
        final String body = "This is Body";

        //when
        final HttpRequest request = HttpRequest.builder(startLine)
            .headers(headers)
            .body(body)
            .build();

        //then
        assertAll(
            () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
            () -> assertThat(request.getPath()).isEqualTo("/hello"),
            () -> assertThat(request.getProtocolVersion()).isEqualTo(HttpVersion.HTTP_1_1),
            () -> assertThat(request.getHeaders().getAllHeaders()).hasSize(2),
            () -> assertThat(request.getHeader("Host"))
                .isEqualTo("www.example.com"),
            () -> assertThat(request.getHeader("Accept"))
                .isEqualTo("text/plain"),
            () -> assertThat(request.getBody()).isEqualTo("This is Body")
        );
    }
}
