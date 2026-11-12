package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {
    private static final String RAW_REQUEST = """
        POST /login?next=/index.html HTTP/1.1
        Host: localhost:8080
        Accept-Language: ko-KR
        Accept: text/html
        Content-Type: application/x-www-form-urlencoded; charset=UTF-8
        Cookie: JSESSIONID=session-id; theme=dark
        Content-Length: 24

        account=user&password=pw""";

    @Test
    void parsesRequestLineHeadersAndBody() throws Exception {
        HttpRequest request = HttpRequest.from(
            new ByteArrayInputStream(RAW_REQUEST.replace("\n", "\r\n")
                .getBytes(StandardCharsets.UTF_8)));

        assertThat(request.requestLine().method()).isEqualTo(HttpMethod.POST);
        assertThat(request.requestLine().path().resource()).isEqualTo("/login");
        assertThat(request.requestLine().path().query()).isEqualTo("next=/index.html");
        assertThat(request.requestLine().protocolVersion().value()).isEqualTo("HTTP/1.1");
        assertThat(request.header("host")).contains("localhost:8080");
        assertThat(request.header("ACCEPT-LANGUAGE")).contains("ko-KR");
        assertThat(request.header("Accept")).contains("text/html");
        assertThat(request.header("Content-Type"))
            .contains("application/x-www-form-urlencoded; charset=UTF-8");
        assertThat(Cookie.from(request.header("Cookie").orElse(""))
            .get("JSESSIONID")).isEqualTo("session-id");
        assertThat(request.body()).isEqualTo("account=user&password=pw");
    }
}
