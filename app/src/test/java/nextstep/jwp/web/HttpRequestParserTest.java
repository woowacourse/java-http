package nextstep.jwp.web;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestParserTest {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    @DisplayName("HTTP 요청으로부터 HttpRequest 객체를 만들어낼 수 있다.")
    void parse() throws IOException {
        // given
        String contentType = "account=gugu&password=password&email=hkkang%40woowahan.com";
        String request = String.join(LINE_SEPARATOR,
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + contentType.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*" + LINE_SEPARATOR,
                contentType);

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        // when
        HttpRequest actual = HttpRequestParser.parse(inputStream);

        // then
        assertThat(actual.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(actual.getRequestUri().toString()).isEqualTo("/register");
        assertThat(actual.getHeaders()).containsKeys(Arrays.array(
                "Host",
                "Connection",
                "Content-Length",
                "Content-Type",
                "Accept"));
        assertThat(actual.getRequestBody()).isNotEmpty();
        assertThat(actual.getRequestBody()).isEqualTo(contentType);
    }

    @Test
    @DisplayName("요청 URL에 QueryParameter가 포함되어 있다면, 요청 URL만을 분리해내고, 파라미터는 따로 Parsing한다.")
    void parseParameter() throws IOException {
        // given
        String request = String.join(LINE_SEPARATOR,
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        // when
        HttpRequest actual = HttpRequestParser.parse(inputStream);

        // then
        assertThat(actual.getParameters()).containsAllEntriesOf(Map.of("account", "gugu", "password", "password"));
    }

    @Test
    @DisplayName("헤더 값으로부터 쿠키를 추출할 수 있다.")
    public void getCookies() throws IOException {
        String request = String.join(LINE_SEPARATOR,
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        // when
        HttpRequest httpRequest = HttpRequestParser.parse(inputStream);
        List<Cookie> cookies = httpRequest.getCookies();

        // then
        assertThat(cookies).containsExactlyInAnyOrder(
                new Cookie("yummy_cookie", "choco"),
                new Cookie("tasty_cookie", "strawberry"),
                new Cookie("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"));
    }

    @Test
    @DisplayName("원하는 쿠키값을 찾을 수 있다.")
    void getCookie() throws IOException {
        String request = String.join(LINE_SEPARATOR,
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        // when
        HttpRequest httpRequest = HttpRequestParser.parse(inputStream);
        Cookie actual = httpRequest.getCookie("JSESSIONID");

        // then
        assertThat(actual).isEqualTo(new Cookie("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"));
    }
}
