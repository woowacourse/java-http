package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.web.Request;
import org.junit.jupiter.api.Test;

class RequestTest {

    private static final String HTTP_GET_REQUEST = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
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
        Request request = Request.parse(createBufferedReader(HTTP_GET_REQUEST));
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getRequestUrl()).isEqualTo("/index.html"),
                () -> assertThat(request.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(request.getHttpHeaders().getHeaders()).containsOnlyKeys("Host", "Connection")
        );
    }

    @Test
    void parseRawBody() throws IOException {
        Request request = Request.parse(createBufferedReader(HTTP_POST_REQUEST));
        assertThat(request.getRequestBody()).isEqualTo("account=gugu&password=password&email=hkkang%40woowahan.com");
    }

    @Test
    void parseBody() throws IOException {
        Request request = Request.parse(createBufferedReader(HTTP_POST_REQUEST));
        Map<String, String> requestBody = request.parseBody();
        assertThat(requestBody).containsOnlyKeys("account", "password", "email");
    }

    @Test
    void getRequestExtension() throws IOException {
        Request request = Request.parse(createBufferedReader(HTTP_GET_REQUEST));
        assertThat(request.getRequestExtension()).isEqualTo("html");
    }

    @Test
    void getDefaultRequestExtension() throws IOException {
        Request request = Request.parse(createBufferedReader("GET /api/login HTTP/1.1 \r\n"));
        assertThat(request.getRequestExtension()).isEqualTo("strings");
    }

    @Test
    void isFileRequest() throws IOException {
        Request request = Request.parse(createBufferedReader(HTTP_GET_REQUEST));
        assertThat(request.isFileRequest()).isTrue();
    }

    @Test
    void isNotFileRequest() throws IOException {
        Request request = Request.parse(createBufferedReader("GET /api/login HTTP/1.1 \r\n"));
        assertThat(request.isFileRequest()).isFalse();
    }

    @Test
    void getQueryParameters() throws IOException {
        Request request = Request.parse(
                createBufferedReader("GET /login?account=gugu&password=password HTTP/1.1 \r\n"));
        assertThat(request.getQueryParameters()).containsKeys("account", "password");
    }

    @Test
    void getEmptyQueryParameters() throws IOException {
        Request request = Request.parse(createBufferedReader("GET /api/login HTTP/1.1 \r\n"));
        assertThat(request.getQueryParameters()).isEmpty();
    }

    @Test
    void isSameRequestUrl() throws IOException {
        Request request = Request.parse(
                createBufferedReader("GET /login?account=gugu&password=password HTTP/1.1 \r\n"));
        assertThat(request.isSameRequestUrl("/login")).isTrue();
    }

    private BufferedReader createBufferedReader(final String httpRequest) {
        InputStreamReader inputStreamReader = new InputStreamReader(
                new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8)));
        return new BufferedReader(inputStreamReader);
    }
}
