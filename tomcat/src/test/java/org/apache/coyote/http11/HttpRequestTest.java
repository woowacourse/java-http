package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {


    @Test
    @DisplayName("HTTP 요청을 받아 메서드를 추출한다.")
    void getMethod() {
        final String requestString= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        HttpRequest request = new HttpRequest(List.of(requestString.split("\r\n")));

        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    @DisplayName("HTTP 요청을 받아 RequestURI를 추출한다.")
    void getRequestURI() {
        final String requestString= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        HttpRequest request = new HttpRequest(List.of(requestString.split("\r\n")));

        assertThat(request.getRequestURI()).isEqualTo("/index.html");
    }
}
