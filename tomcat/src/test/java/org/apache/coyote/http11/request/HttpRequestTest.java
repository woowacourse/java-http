package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("http request의 첫 줄이 빈 문자열이면, request line이 존재하지 않다고 판단하여 예외를 발생시킨다.")
    @Test
    void cannotFindRequestLine() {
        // given
        String httpRequestMessage = String.join("\r\n",
                "",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());

        // when & then
        assertThatThrownBy(() -> new HttpRequest(inputStream)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("request line이 존재하지 않습니다.");
    }

    @DisplayName("http request로부터 request path를 파싱할 수 있다.")
    @Test
    void getPath() throws IOException {
        // given
        String httpRequestMessage = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        String path = request.getPath();

        // then
        assertThat(path).isEqualTo("/index.html");
        inputStream.close();
    }

    @DisplayName("http request로부터 Cookie를 파싱할 수 있다.")
    @Test
    void getCookie() throws IOException {
        // given
        String httpRequestMessage = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        HttpCookie cookie = request.getCookie();

        // then
        assertAll(
                () -> assertThat(cookie.get("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry"),
                () -> assertThat(cookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
        inputStream.close();
    }

    @DisplayName("request body가 쿼리스트링 형식일 때, key, value 쌍을 만들 수 있다.")
    @Test
    void getBodyQueryString() throws IOException {
        // given
        final String httpRequestMessage = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password ",
                "");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        Map<String, String> bodyQueryString = request.getBodyQueryString();

        // then
        assertAll(
                () -> assertThat(bodyQueryString.get("account")).isEqualTo("gugu"),
                () -> assertThat(bodyQueryString.get("password")).isEqualTo("password")
        );
        inputStream.close();
    }
}
