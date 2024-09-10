package com.techcourse.handler;

import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpXW3UrlEncodedBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloHandlerTest {

    @Test
    @DisplayName("루트 경로 요청을 처리할 수 있다.")
    void canHandle() {
        HelloHandler helloHandler = new HelloHandler();

        boolean result = helloHandler.canHandle(createHttpRequest("GET / HTTP/1.1"));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("루트 경로가 아닌 요청은 처리할 수 없다.")
    void cantHandle() {
        HelloHandler helloHandler = new HelloHandler();

        boolean result = helloHandler.canHandle(createHttpRequest("GET /login HTTP/1.1"));

        assertThat(result).isFalse();
    }

    private HttpRequest createHttpRequest(String startLine) {
        return HttpRequest.createHttp11Message(startLine, Header.empty(), new HttpXW3UrlEncodedBody(null));
    }
}
