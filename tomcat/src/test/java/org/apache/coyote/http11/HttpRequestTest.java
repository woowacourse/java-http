package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpRequestTest {

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
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(httpRequestMessage.getBytes()));

        // when
        String path = request.getPath();

        // then
        assertThat(path).isEqualTo("/index.html");
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
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(httpRequestMessage.getBytes()));

        // when
        HttpCookie cookie = request.getCookie();

        // then
        assertAll(
                () -> assertThat(cookie.get("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry"),
                () -> assertThat(cookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
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
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(httpRequestMessage.getBytes()));

        // when
        Map<String, String> bodyQueryString = request.getBodyQueryString();

        // then
        assertAll(
                () -> assertThat(bodyQueryString.get("account")).isEqualTo("gugu"),
                () -> assertThat(bodyQueryString.get("password")).isEqualTo("password")
        );
    }

    @DisplayName("HttpRequest가 GET 요청이면 true를 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "GET, true",
            "POST, false",
    })
    void isGetMethod(String method, boolean expected) throws IOException {
        // given
        String httpRequestMessage = String.join("\r\n",
                method+ " /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest requset = new HttpRequest(new ByteArrayInputStream(httpRequestMessage.getBytes()));

        // when
        boolean isGetMethod = requset.isGetMethod();

        // then
        assertThat(isGetMethod).isEqualTo(expected);
    }

    @DisplayName("HttpRequest가 POST 요청이면 true를 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "POST, true",
            "GET, false",
    })
    void isPostMethod(String method, boolean expected) throws IOException {
        // given
        String httpRequestMessage = String.join("\r\n",
                method+ " /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(httpRequestMessage.getBytes()));

        // when
        boolean isPostMethod = request.isPostMethod();

        // then
        assertThat(isPostMethod).isEqualTo(expected);
    }
}
