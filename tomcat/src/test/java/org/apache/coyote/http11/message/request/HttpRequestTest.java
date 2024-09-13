package org.apache.coyote.http11.message.request;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.http11.message.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 객체를 생성한다.")
    void createTest() {
        // given
        String requestLine = "GET /index.html HTTP/1.1";
        HttpHeaders headers = new HttpHeaders();
        HttpRequestBody body = new HttpRequestBody();

        // when
        HttpRequest request = HttpRequest.of(requestLine, headers, body);

        // then
        assertAll(
                () -> assertEquals(HttpMethod.GET, request.getMethod()),
                () -> assertEquals("/index.html", request.getUrlPath())
        );
    }
}
