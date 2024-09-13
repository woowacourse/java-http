package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpMethod;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void isMethod() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        assertThat(requestLine.isMethod(HttpMethod.GET)).isTrue();
    }

    @Test
    void getContentType() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        assertThat(requestLine.getContentType()).isEqualTo("text/html");
    }

    @Test
    void isStaticRequest() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        assertThat(requestLine.isStaticRequest()).isTrue();
    }

    @Test
    void hasQueryParam() {
        RequestLine requestLine = new RequestLine("GET /index.html?name=kirby HTTP/1.1");
        assertThat(requestLine.hasQueryParam()).isTrue();
    }

    @Test
    void getQueryParam() {
        RequestLine requestLine = new RequestLine("GET /index.html?name=kirby HTTP/1.1");
        assertThat(requestLine.getQueryParam("name")).isEqualTo("kirby");
    }
}
