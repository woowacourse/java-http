package org.apache.coyote.http11.model.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;

import org.apache.coyote.exception.InvalidHttpRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("HttpRequest 객체를 올바르게 생성한다.")
    @Test
    void createHttpRequest() throws IOException {
        String request = "POST /register HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Content-Length: 58\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n"
                + "Accept: */*\n"
                + "\n"
                + "account=gugu&password=password&email=hkkang%40woowahan.com\n";

        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        assertAll(
                () -> assertThat(httpRequest.getMethod().getValue()).isEqualTo("POST"),
                () -> assertThat(httpRequest.getUri()).isEqualTo("/register"),
                () -> assertThat(httpRequest.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getHeaderValue("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(httpRequest.getHeaderValue("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(httpRequest.getHeaderValue("Content-Length")).isEqualTo("58"),
                () -> assertThat(httpRequest.getHeaderValue("Content-Type")).isEqualTo(
                        "application/x-www-form-urlencoded"),
                () -> assertThat(httpRequest.getHeaderValue("Accept")).isEqualTo("*/*"),
                () -> assertThat(httpRequest.getBodyValue("account")).isEqualTo("gugu"),
                () -> assertThat(httpRequest.getBodyValue("password")).isEqualTo("password"),
                () -> assertThat(httpRequest.getBodyValue("email")).isEqualTo("hkkang%40woowahan.com"),
                () -> assertThat(httpRequest.getCookieValue("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(httpRequest.getCookieValue("tasty_cookie")).isEqualTo("strawberry"),
                () -> assertThat(httpRequest.getCookieValue("JSESSIONID")).isEqualTo(
                        "656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @DisplayName("RequestLine의 요소가 부족한 경우 예외가 발생한다.")
    @Test
    void validateRequestLineValues() {
        String request = "GET /index.html";

        assertThatThrownBy(() -> HttpRequestGenerator.generate(request))
                .isInstanceOf(InvalidHttpRequestException.class)
                .hasMessage("잘못된 형식의 Request Line입니다.");
    }

    @DisplayName("Request header가 없는 경우 예외가 발생한다.")
    @Test
    void validateEmptyRequestHeader() {
        String request = "GET / /index.html";

        assertThatThrownBy(() -> HttpRequestGenerator.generate(request))
                .isInstanceOf(InvalidHttpRequestException.class)
                .hasMessage("Http Request Header가 비어있습니다.");
    }

    @DisplayName("Request body가 없는 요청으로 HttpRequest 객체를 올바르게 생성한다.")
    @Test
    void createHttpRequestWithoutBody() throws IOException {
        String request = "POST /register HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n";

        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        assertAll(
                () -> assertThat(httpRequest.getMethod().getValue()).isEqualTo("POST"),
                () -> assertThat(httpRequest.getUri()).isEqualTo("/register"),
                () -> assertThat(httpRequest.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getHeaderValue("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(httpRequest.getHeaderValue("Connection")).isEqualTo("keep-alive")
        );
    }
}
