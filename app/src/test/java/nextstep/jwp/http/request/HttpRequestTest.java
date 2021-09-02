package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import nextstep.jwp.http.cookie.HttpCookie;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("InputStream으로부터 문자열을 읽어 HttpRequest를 파싱한다")
    @Test
    void of() {
        final String expectedRequestLine = "POST /register HTTP/1.1 ";
        final String expectedRequestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final String inputRequest = mockRequest(expectedRequestLine, expectedRequestBody);

        try (final InputStream inputStream = new ByteArrayInputStream(inputRequest.getBytes())) {
            HttpRequest parsedRequest = HttpRequest.of(inputStream);

            QueryParams params = parsedRequest.requestParam();
            assertThat(params.get("account")).isEqualTo("gugu");
            assertThat(params.get("password")).isEqualTo("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("쿠기가 있는 경우, 쿠키 값을 반환한다")
    @Test
    void cookie() {
        RequestLine requestLine = RequestLine.of("GET / HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(
                Arrays.asList(
                        "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46")
        );

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        assertThat(httpRequest.httpCookie().getAttributes("yummy_cookie")).isEqualTo("choco");
        assertThat(httpRequest.httpCookie().getAttributes("tasty_cookie")).isEqualTo("strawberry");
    }

    @DisplayName("쿠기가 없는 경우, 빈 쿠키 객체를 반환한다")
    @Test
    void emptyCookie() {
        RequestLine requestLine = RequestLine.of("GET / HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Collections.emptyList());

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        assertThat(httpRequest.httpCookie()).isEqualTo(HttpCookie.EMPTY);
    }

    @DisplayName("SessionId를 반환한다")
    @Test
    void getSession() {
        RequestLine requestLine = RequestLine.of("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        ));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        assertThat(httpRequest.getSessionId()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    private String mockRequest(String expectedRequestLine, String expectedRequestBody) {
        return String.join("\r\n",
                expectedRequestLine,
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + expectedRequestBody.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                expectedRequestBody,
                "");
    }
}
