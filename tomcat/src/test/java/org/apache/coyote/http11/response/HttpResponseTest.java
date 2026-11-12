package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpResponseTest {
    @Test
    void createsOkResponseWithContentLength() {
        HttpResponse response = HttpResponse.ok("Hello world!", "html");

        assertThat(response.toString()).isEqualTo(String.join("\r\n",
            "HTTP/1.1 200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: 12",
            "",
            "Hello world!"));
        assertThat(response.header("content-type")).contains("text/html;charset=utf-8");
        assertThat(response.header("CONTENT-LENGTH")).contains("12");
    }

    @Test
    void createsRedirectResponseWithLocationAndCookie() {
        HttpResponse response = HttpResponse.redirectWithCookie(
            "/index.html", "body", "html", "session-id");

        assertThat(response.toString()).contains("HTTP/1.1 302 Found")
            .contains("Location: /index.html")
            .contains("Set-Cookie: JSESSIONID=session-id;")
            .contains("Content-Length: 4");
    }
}
