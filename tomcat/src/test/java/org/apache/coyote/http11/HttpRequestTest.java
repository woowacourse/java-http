package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private BufferedReader bufferedReader;
    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");
        this.bufferedReader = new BufferedReader(new StringReader(httpRequest));
        this.httpRequest = new HttpRequest(bufferedReader);
    }

    @Test
    @DisplayName("요청 헤더에서 정보를 얻어올 수 있다.")
    void parseRequestHeader() {
        String path = httpRequest.getPath();
        String method = httpRequest.getMethod();
        Map<String, String> actualParsedQueryString = Map.of("account", "gugu", "password", "password");
        Map<String, String> expectedParsedQueryString = httpRequest.parseQueryString().get();

        assertAll(
                () -> assertEquals("/login.html", path),
                () -> assertEquals("GET", method),
                () -> assertThat(actualParsedQueryString).usingRecursiveComparison()
                        .isEqualTo(expectedParsedQueryString)
        );
    }
}
