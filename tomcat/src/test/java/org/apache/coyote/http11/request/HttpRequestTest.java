package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Stream;
import org.apache.coyote.http11.common.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("HTTP 요청의 메서드와 경로, 버전, 헤더를 파싱한다.")
    @Test
    void httpRequestMethodPathVersionParse() {
        // given
        String body = "Hello, World!";
        String rawRequest = Stream.of(
                "GET /index.html?query=value HTTP/1.1",
                "Cookie: JSESSIONID=1234",
                "Content-Length: " + body.length(),
                "",
                body
        ).reduce((a, b) -> a + "\r\n" + b).orElse("");

        // when
        HttpRequest request = HttpRequest.from(rawRequest);
        request.setBody(body);

        // then
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getPath()).isEqualTo("/index.html"),
                () -> assertThat(request.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(request.getHeaders()).containsEntry("Cookie", "JSESSIONID=1234"),
                () -> assertThat(request.getHeaders()).containsEntry("Content-Length", String.valueOf(body.length())),
                () -> assertThat(request.getBody()).isEqualTo(body)
        );
    }

    @DisplayName("Body가 없을 때 HTTP 요청의 메서드와 경로, 버전, 헤더를 파싱한다.")
    @Test
    void httpRequestMethodPathVersionParse2() {
        // given
        String body = "Hello, World!";
        String rawRequest = Stream.of(
                "GET /index.html?query=value HTTP/1.1",
                "Cookie: JSESSIONID=1234",
                "Content-Length: " + body.length()
        ).reduce((a, b) -> a + "\r\n" + b).orElse("");

        // when
        HttpRequest request = HttpRequest.from(rawRequest);

        // then
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getPath()).isEqualTo("/index.html"),
                () -> assertThat(request.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(request.getHeaders()).containsEntry("Cookie", "JSESSIONID=1234")
        );
    }
}
