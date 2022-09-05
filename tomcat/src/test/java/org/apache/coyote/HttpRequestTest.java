package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.request.HttpRequestParser;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private static final String HTTP_GET_REQUEST = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

    private static final String HTTP_GET_REQUEST_WITH_COOKIE = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Cookie: JSESSIONID=cookie; just=do_it ",
            "",
            "");

    private static final String HTTP_POST_REQUEST = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 58 ",
            "Content-Type: application/x-www-form-urlencoded ",
            "Accept: */* ",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com ");

    @Test
    void parse() throws IOException {
        HttpRequest httpRequest = HttpRequestParser.parse(createBufferedReader(HTTP_GET_REQUEST));
        assertAll(
                () -> assertThat(httpRequest.getHttpRequestLine().getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getHttpRequestLine().getRequestUrl()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getHttpRequestLine().getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getHttpHeaders().getHeaders()).containsOnlyKeys("Host", "Connection")
        );
    }

    @Test
    void getParameter() throws IOException {
        HttpRequest httpRequest = HttpRequestParser.parse(createBufferedReader(HTTP_POST_REQUEST));
        assertAll(
                () -> assertThat(httpRequest.getParameter("account")).isEqualTo("gugu"),
                () -> assertThat(httpRequest.getParameter("password")).isEqualTo("password"),
                () -> assertThat(httpRequest.getParameter("email")).isEqualTo("hkkang%40woowahan.com")
        );
    }

    @Test
    void getSession() throws IOException {
        HttpRequest httpRequest = HttpRequestParser.parse(createBufferedReader(HTTP_GET_REQUEST_WITH_COOKIE));
        assertThat(httpRequest.getSession().get()).isEqualTo("cookie");
    }

    private BufferedReader createBufferedReader(final String httpRequest) {
        InputStreamReader inputStreamReader = new InputStreamReader(
                new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8)));
        return new BufferedReader(inputStreamReader);
    }
}
