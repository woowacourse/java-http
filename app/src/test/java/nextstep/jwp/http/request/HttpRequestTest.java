package nextstep.jwp.http.request;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void of() {
        final String expectedRequestLine = "POST /register HTTP/1.1 ";
        final String expectedRequestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final String inputRequest = mockRequest(expectedRequestLine, expectedRequestBody);

        try (final InputStream inputStream = new ByteArrayInputStream(inputRequest.getBytes())) {
            HttpRequest parsedRequest = HttpRequest.of(inputStream);

            RequestLine requestLine = parsedRequest.requestLine();
            assertThat(requestLine).isEqualTo(RequestLine.of(expectedRequestLine));

            RequestHeaders requestHeaders = parsedRequest.requestHeaders();
            assertThat(requestHeaders.contentLength()).isEqualTo(expectedRequestBody.length());

            String body = parsedRequest.requestBody();
            assertThat(body).isEqualTo(expectedRequestBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("쿠기가 있는 경우, 쿠키 값을 반환한다.")
    @Test
    void cookie() {
        RequestLine requestLine = RequestLine.of("GET / HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(
                Arrays.asList("Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46")
        );

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        assertThat(httpRequest.httpCookie().getAttributes("yummy_cookie")).isEqualTo("choco");
        assertThat(httpRequest.httpCookie().getAttributes("tasty_cookie")).isEqualTo("strawberry");

    }

    @DisplayName("쿠기가 없는 경우, 빈 쿠키 객체를 반환한다.")
    @Test
    void emptyCookie() {
        RequestLine requestLine = RequestLine.of("GET / HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Collections.emptyList());

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        assertThat(httpRequest.httpCookie()).isEqualTo(HttpCookie.EMPTY);
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